---
layout: intro
class: section-intro
---

# SQL DSL

## The query is the interesting part

---
class: code-slide
---

# A query reads like the SQL it sends

<DrawnAnnotation type="circle" text="selectAll()" label="a `SELECT`, spelled out" :geometry="{ label: { x: 0.5, y: 0.385 } }" :on="1">
<DrawnAnnotation type="underline" text="orderBy(DriverTable.lastName)" label="a column, not a string" :geometry="{ label: { x: 0.5, y: 0.385 } }" :at="2">

```kotlin no-compile
override fun findAll(): List<Driver> =
    DriverTable.selectAll()
        .orderBy(DriverTable.lastName)
        .map(ResultRow::toDriver)
```

```sql
SELECT driver.id, driver.first_name, driver.last_name
FROM driver
ORDER BY driver.last_name ASC
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Same method every repository has, and both halves are on the slide
- Click one: selectAll is the SELECT, nothing is deferred, nothing is proxied
- Click two: I order by a column value, so a rename is a compiler error
- Say it: what you see is what runs - and that is the whole section
- Handover: one piece is still missing, the mapping -> toDriver
-->

---
class: code-slide
---

# The row becomes my domain in code I own

```kotlin no-compile
private fun ResultRow.toDriver() = Driver(
    id = this[DriverTable.id],
    firstName = this[DriverTable.firstName],
    lastName = this[DriverTable.lastName],
)
```

<!--
- One extension function, private to the repository - that is the entire mapping layer
- Point at the types: each column knows its type, so Driver is built type safe
- No annotations, no reflection, no naming strategy deciding this for me
- Driver stays a plain data class with vals - it never learns about the database
- Handover: now the same query, with a condition -> findById
-->

---
class: code-slide
---

# The where clause is Kotlin the compiler checks

<DrawnAnnotation type="box" text="where { DriverTable.id eq id }" label="typesafe conditions" :geometry="{ label: { x: 0.5, y: 0.43 } }" :at="1">

```kotlin no-compile
override fun findById(id: Long): Driver? = DriverTable
    .selectAll()
    .where { DriverTable.id eq id }
    .map { it.toDriver() }
    .firstOrNull()
```

```sql
SELECT driver.id, driver.first_name, driver.last_name
FROM driver
WHERE driver.id = ?
```

</DrawnAnnotation>

<!--
- Click: eq is an infix function on Column<Long>, so it only accepts a Long
- Compare to JPQL: a string parsed at startup, at best - here it is parsed by the compiler
- Point at the parameter marker in the SQL - bound, never concatenated
- Ask the room: who has shipped a typo in a JPQL query?
- Handover: reading is the easy half -> writing
-->

---
class: code-slide
---

# Insert hands the row straight back

<DrawnAnnotation type="circle" text="insertReturning" label="no second query" :geometry="{ label: { x: 0.5, y: 0.435 } }" :on="1">
<DrawnAnnotation type="underline" text="statement[firstName] = entity.firstName" label="typesafe row mapping" :geometry="{ label: { x: 0.5, y: 0.435 } }" :at="2">

```kotlin no-compile
override fun create(entity: Driver): Driver = DriverTable
    .insertReturning { statement ->
        statement[firstName] = entity.firstName
        statement[lastName] = entity.lastName
    }.first().toDriver()
```

```sql
INSERT INTO driver (first_name, last_name)
VALUES (?, ?)
RETURNING driver.id, driver.first_name, driver.last_name
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Click one: insertReturning maps onto RETURNING, so the generated id comes back with the insert
- No flush, no detached instance, no wondering when the id appears
- Click two: the index is the column and the value has to match its type
- Note what is not set: I never mention id, because the database generates it
- Handover: same shape for changing a row -> update
-->

---
class: code-slide
---

# An update is a statement I wrote, not a side effect

<DrawnAnnotation type="circle" text=".update(" label="never dirty checking" :geometry="{ label: { x: 0.5, y: 0.52 } }" :on="1">
<DrawnAnnotation type="box" text="where = { DriverTable.id eq entity.id }" label="I decide which rows" :geometry="{ label: { x: 0.5, y: 0.52 } }" :at="2">

```kotlin no-compile
override fun update(entity: Driver): Driver {
    DriverTable.update(where = { DriverTable.id eq entity.id }) { statement ->
        statement[firstName] = entity.firstName
        statement[lastName] = entity.lastName
    }
    return entity
}
```

```sql
UPDATE driver
SET first_name = ?, last_name = ?
WHERE driver.id = ?
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Click one: nothing is written because an object changed - it is written because I called update
- No persistence context, no flush at a transaction boundary I forgot about
- Click two: the where is mine, so the blast radius is on screen
- Say it: no entity was loaded to write this row
- Handover: two statements for one intent is annoying -> upsert
-->

---
class: code-slide
---

# Upsert is one statement, and I name the conflict key

<DrawnAnnotation type="circle" text="upsertReturning" label="`ON CONFLICT DO UPDATE`" :geometry="{ label: { x: 0.5, y: 0.48 } }" :on="1">
<DrawnAnnotation type="underline" text="(DriverTable.id)" label="the conflict key" :geometry="{ label: { x: 0.5, y: 0.48 } }" :at="2">

```kotlin no-compile
override fun upsert(entity: Driver): Driver = DriverTable
    .upsertReturning(DriverTable.id) { statement ->
        entity.id?.let { statement[id] = it }
        statement[firstName] = entity.firstName
        statement[lastName] = entity.lastName
    }.map(ResultRow::toDriver).first()
```

```sql
INSERT INTO driver (id, first_name, last_name)
VALUES (?, ?, ?)
ON CONFLICT (id) DO UPDATE
SET first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name
RETURNING driver.id, driver.first_name, driver.last_name
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Click one: one round trip, and the stored row comes back with it
- This is the JPA escape hatch slide from section one - except it is still the DSL, still typed
- Click two: the conflict key is an argument I pass, any unique column works
- Say it out loud: no merge, no save that might mean two different things
- Handover: last one, and the return type is the point -> delete
-->

---
class: code-slide
---

# Delete tells me how many rows it hit

<DrawnAnnotation type="underline" text="deleteWhere { id eq entity.id } > 0" label="a row count, not a void" :geometry="{ label: { x: 0.5, y: 0.4 } }" :at="1">

```kotlin no-compile
override fun delete(entity: Driver): Boolean {
    requireNotNull(entity.id) { "`id` must not be null" }
    return DriverTable.deleteWhere { id eq entity.id } > 0
}
```

```sql
DELETE FROM driver WHERE driver.id = ?
```

</DrawnAnnotation>

<!--
- Click: the statement returns the number of affected rows, so I can answer "did it exist?"
- One statement, no cascade deciding to visit tables I did not mention
- Whole repository now: five statements, five SQL logs, nothing in between
- Handover: this is plain Exposed - now let Spring own the transactions -> section four
-->

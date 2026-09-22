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
- Handover: one table is easy - the interesting part starts with joins -> taxi rides
-->

---
class: code-slide
---

# A join is written, not configured

<DrawnAnnotation type="underline" text=".join(TaxiTable, INNER, TaxiRideTable.taxiId, TaxiTable.id)" label="the join lives in the query" :geometry="{ label: { x: 0.5, y: 0.45 } }" :on="1">
<DrawnAnnotation type="underline" text=".join(PassengerTable, INNER, TaxiRideTable.passengerId, PassengerTable.id)" label="no mapping decides this for me" :geometry="{ label: { x: 0.5, y: 0.45 } }" :at="2">

```kotlin no-compile
override fun findAll(): List<TaxiRide> = TaxiRideTable
    .join(TaxiTable, INNER, TaxiRideTable.taxiId, TaxiTable.id)
    .join(PassengerTable, INNER, TaxiRideTable.passengerId, PassengerTable.id)
    .selectAll()
    .map(ResultRow::toTaxiRide)
```

```sql
SELECT taxi_ride.*, taxi.*, passenger.*
FROM taxi_ride
INNER JOIN taxi ON taxi_ride.taxi_id = taxi.id
INNER JOIN passenger ON taxi_ride.passenger_id = passenger.id
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- A ride needs its taxi and its passenger, so this is one statement with two joins
- Click one and two: both joins are arguments, columns on both sides, checked by the compiler
- No FetchType, no entity graph, no annotation somewhere else deciding this
- Say it: three tables, one query - the N+1 from section one cannot happen here
- Handover: I do not want to retype this join -> pull it out
-->

---
class: code-slide
---

# The join is a value I can hand around

<DrawnAnnotation type="box" text="private fun taxiRides(): Query" label="a `Query` is just a value" :geometry="{ label: { x: 0.5, y: 0.52 } }" :on="1">
<DrawnAnnotation type="circle" text="taxiRides()" occurrence="2" label="every read starts here" :geometry="{ label: { x: 0.5, y: 0.52 } }" :at="2">

```kotlin no-compile
private fun taxiRides(): Query = TaxiRideTable
    .join(TaxiTable, INNER, TaxiRideTable.taxiId, TaxiTable.id)
    .join(PassengerTable, INNER, TaxiRideTable.passengerId, PassengerTable.id)
    .selectAll()

override fun findAll(): List<TaxiRide> = taxiRides().map(ResultRow::toTaxiRide)
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Click one: a query is a normal Kotlin value, so I can name it and return it
- Nothing runs yet - it runs when I iterate it
- Click two: findAll is now one line, and the join is defined in exactly one place
- Say it: this is reuse by function, not by inheritance or by a named query string
- Handover: and every other read builds on the same value -> filtering
-->

---
class: code-slide
---

# Filters are added to the query I already have

<DrawnAnnotation type="underline" text="(TaxiRideTable.passengerId eq passengerId) and" label="two typed conditions, one expression" :geometry="{ label: { x: 0.5, y: 0.515 } }" :at="1">
<DrawnAnnotation type="underline" text="(TaxiRideTable.status eq status)" :sequential="false" :at="1">

```kotlin no-compile
fun findAllByPassengerIdAndStatus(passengerId: Long, status: RideStatus) =
    taxiRides()
        .where {
            (TaxiRideTable.passengerId eq passengerId) and
                (TaxiRideTable.status eq status)
        }
        .map(ResultRow::toTaxiRide)
```

```sql
SELECT taxi_ride.*, taxi.*, passenger.*
FROM taxi_ride
INNER JOIN taxi ON taxi_ride.taxi_id = taxi.id
INNER JOIN passenger ON taxi_ride.passenger_id = passenger.id
WHERE taxi_ride.passenger_id = ? AND taxi_ride.status = ?
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Same join as before, one where clause on top - no new query, no new repository method name magic
- Click: and is an infix function, so the condition is an expression I could also store in a val
- status is an enum and the column knows it - no string, no converter to register
- Compare: no derived query name to spell correctly, no JPQL to parse at startup
- Handover: conditions are the easy case - now an aggregate -> countByDriverId
-->

---
class: code-slide
---

# An aggregate is a value, so I reuse it

<DrawnAnnotation type="box" text="val driverRideCount = rideCount.alias(&quot;driverRideCount&quot;)" label="`COUNT(...)` as a Kotlin value" :geometry="{ label: { x: 0.5, y: 0.85 } }" :on="1">
<DrawnAnnotation type="underline" text="having { rideCount greaterEq minCount }" label="filter on it" :geometry="{ label: { x: 0.5, y: 0.85 } }" :on="2">
<DrawnAnnotation type="underline" text="orderBy(rideCount, SortOrder.DESC)" label="sort by it" :geometry="{ label: { x: 0.5, y: 0.85 } }" :on="3">
<DrawnAnnotation type="underline" text="it[driverRideCount]" label="read it back, typed" :geometry="{ label: { x: 0.5, y: 0.85 } }" :at="4">

```kotlin no-compile
val rideCount = TaxiRideTable.id.count()
val driverRideCount = rideCount.alias("driverRideCount")

fun countByDriverId(driverId: Long, minCount: Long = 0) = TaxiRideTable
    .join(TaxiTable, INNER, TaxiRideTable.taxiId, TaxiTable.id)
    .join(DriverTable, INNER, TaxiTable.driverId, DriverTable.id)
    .select(DriverTable.id, DriverTable.firstName,
            DriverTable.lastName, driverRideCount)
    .where { TaxiTable.driverId eq driverId }
    .groupBy(DriverTable.id, DriverTable.firstName, DriverTable.lastName)
    .having { rideCount greaterEq minCount }
    .orderBy(rideCount, SortOrder.DESC)
    .map { DriverWithRideCount(it.toDriver(), it[driverRideCount]) }
```

</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>

<!--
- The complex query, and it still fits on one slide
- Click one: the aggregate is a val - a COUNT I can pass around like any other value
- The alias only exists so I can read the column back out of the row
- Clicks two to four: the same expression in having, in the sort, and when reading the row back
- Say it: try that with a string based query - you repeat the expression three times and hope
- Point at the select list: I chose the columns, so I know what crosses the wire
- Handover: one more value I can name - a whole query -> the subselect
-->

---
class: code-slide
---

# A subselect is just another query I pass in

<DrawnAnnotation type="circle" text="notInSubQuery" label="`NOT IN (SELECT ...)`" :geometry="{ label: { x: 0.5, y: 0.625 } }" :on="1">
<DrawnAnnotation type="box" text="private fun driverIdsWithActiveRides() = TaxiRideTable" label="a query, named and reusable" :geometry="{ label: { x: 0.5, y: 0.625 } }" :at="2">

```kotlin no-compile
fun findAllWithoutActiveRides(): List<Driver> = DriverTable
    .selectAll()
    .where { DriverTable.id notInSubQuery driverIdsWithActiveRides() }
    .map(ResultRow::toDriver)

private fun driverIdsWithActiveRides() = TaxiRideTable
    .join(TaxiTable, INNER, TaxiRideTable.taxiId, TaxiTable.id)
    .select(TaxiTable.driverId)
    .where { TaxiRideTable.status inList listOf(REQUESTED, IN_PROGRESS) }
```

```sql
SELECT driver.id, driver.first_name, driver.last_name FROM driver
WHERE driver.id NOT IN (
    SELECT taxi.driver_id FROM taxi_ride
    INNER JOIN taxi ON taxi_ride.taxi_id = taxi.id
    WHERE taxi_ride.status IN (?, ?))
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- The question: which drivers are free right now - no requested, no in progress ride
- Click one: notInSubQuery takes a query, so the nesting is an argument, not a string
- Click two: the inner query is its own named function, typed and testable on its own
- Say it: one statement, one round trip - no loading drivers and filtering them in Kotlin
- Handover: that is the DSL - next, who owns the transaction -> Spring
-->

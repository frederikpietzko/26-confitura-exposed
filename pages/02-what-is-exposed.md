---
layout: intro
class: section-intro
---

# What is Exposed?

## JetBrains' Kotlin SQL Library

---
class: bridge-slide
---

# Exposed sits between Kotlin and SQL - and hides neither

<ExposedBridge />

<!--
- Left: my domain stays mine - data classes, val, no annotations, no compiler plugins
- Right: the database stays the database - schemas, tables, rows, SQL
- Middle is the whole talk: Exposed is a library, not a runtime that owns my objects
  - Two ways to talk to the driver: JDBC today, R2DBC for coroutines
  - Two ways to write queries: the Query DSL, or the DAO if you want entities
  - Integrations: Spring Boot is the one we will use, Ktor exists too
-  nothing here translates my object graph into SQL behind my back
  - What you see is what runs, both directions
-->

---
class: module-slide
---

# You pick the modules - nothing else comes along

<ModuleList>
  <ModuleRow name="exposed-core" tag="required">Type-safe SQL DSL</ModuleRow>
  <ModuleRow name="exposed-dao" tag="optional">Entity API on top of the DSL</ModuleRow>
  <ModuleRow name="exposed-jdbc">for blocking drivers</ModuleRow>
  <ModuleRow name="exposed-r2dbc">for non-blocking drivers, coroutines</ModuleRow>
  <ModuleRow name="exposed-spring-boot4-starter">Datasource and transactions wired up</ModuleRow>
  <ModuleRow name="exposed-java-time, -json, -money" muted>Column types you opt into</ModuleRow>
</ModuleList>

<!--
- Point at the first line and say it out loud: core is the whole library, everything else is a choice
  - No base class, no agent, no compiler plugin in this list
- exposed-dao is the one people expect to be mandatory - it is not
  - We will not use it today, the DSL section is the talk
- jdbc versus r2dbc is one dependency swap, not a rewrite of my queries
- The Spring starter is the boring one: datasource, transaction manager, done - section four
- Bottom row: the integrations exist so I don't have to hand-roll common column types (but you totally can)
- Handover: enough inventory, let us start defining tables -> yes Tables not Entities
-->

---
class: code-slide
---

# A table is an object, not an annotated class

<DrawnAnnotation type="circle" text="Table(&quot;driver&quot;)" label="override the table name" :at="1">

```kotlin no-compile
object DriverTable : Table("driver")
```

</DrawnAnnotation>

<!--
- This is the demo project's driver table, built up in three steps
- Read the first line out loud: a Kotlin object that IS the driver table
  - No @Entity, no @Id, no @Column, no compiler plugin, no base entity class
- Click: the table name is a string argument I pass
  - Not a naming strategy, not a dialect default, not something I have to guess
- Say it: this object is a description of a table, not a place to put my domain data
  - My domain classes stay plain data classes, they never appear on these slides
- Handover: a table without columns is not worth much -> give it an id
-->

---
class: code-slide magicMove: true
---

# The primary key is an override, not an annotation

<DrawnAnnotation type="box" text="override val primaryKey = PrimaryKey(id)" label="define primary keys" :at="1">

```kotlin no-compile
object DriverTable : Table("driver") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
}
```

</DrawnAnnotation>

<!--
- The id is a val with a type: a Long column named id, auto incremented
  - The column name is the string, the Kotlin name is mine - they are allowed to differ
- Click: primaryKey is an override
  - Table declares it, so the compiler is the one reminding me the table needs one
  - No @Id, no @GeneratedValue, no guessing which field the provider picked
- Say it out loud: this is DDL written in Kotlin, and it reads like DDL
- Handover: now the interesting part, the columns -> and their types
-->

---
class: code-slide magicMove: true
---

# A column carries its SQL type and its length

<DrawnAnnotation type="underline" text="varchar(&quot;first_name&quot;, 50)" label="column definitions mirror database types" :at="1">

```kotlin no-compile
object DriverTable : Table("driver") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
}
```

</DrawnAnnotation>

<!--
- This is now exactly the file the demo project compiles - same table, same columns
- Click: varchar with a length, because the database column has a length
  - In JPA that would be @Column(length = 50) and only if I remembered to write it
  - Here I cannot forget it: the function asks me for the length
- Point at the types: firstName is a Column<String>, id is a Column<Long>
  - The table object is a value I can pass around, not a mapping the framework reads
- Ask the room: who has ever been surprised by the DDL their entities generated?
- Handover: now that the table exists as a value, I can use it to build queries -> SQL DSL
-->


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
class: code-slide
magicMove: true
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
class: code-slide
magicMove: true
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
- Handover: writing the id by hand gets old -> the second table picks a base class
-->

---
class: code-slide
---

# Pick a base class and the id comes with it

<DrawnAnnotation type="circle" text="LongIdTable(&quot;taxi&quot;)" label="id and primary key included" :at="1">

```kotlin no-compile
object TaxiTable : LongIdTable("taxi")
```

</DrawnAnnotation>

<!--
- Second table of the demo project, same build-up, three steps
- Click: LongIdTable is Table plus an auto incrementing Long id and its primary key
  - UUIDTable, IntIdTable, the same idea with another id type
  - Still a choice I make by picking a supertype, not magic a provider applies to me
- Say it: the boilerplate I wrote on the driver table is a base class, nothing more
- Handover: now the column this table is actually interesting for -> JSON
-->

---
class: code-slide
magicMove: true
---

# A JSON column is just another column type

<DrawnAnnotation type="underline" text="json&lt;MakeAndModel&gt;(&quot;make_and_model&quot;, format)" label="the serializer is an argument, not a config file" :at="1">

```kotlin no-compile
val format = Json { prettyPrint = true }

object TaxiTable : LongIdTable("taxi") {
    val makeAndModel = json<MakeAndModel>("make_and_model", format)
}
```

</DrawnAnnotation>

<!--
- exposed-json is one of those opt-in modules from two slides ago
- Click: the column type is json, the Kotlin type is my own data class
  - Here it is kotlinx.serialization, and the Json instance is handed to the column
  - Jackson works just as well - the module takes a serializer, it does not pick one for me
- Point at it: I can see which serializer this column uses by reading the column
  - No @Type, no @Converter registered somewhere else in the project
- Handover: one column left, and it points at the other table -> the reference
-->

---
class: code-slide
magicMove: true
---

# A foreign key points at a column, not at an object

<DrawnAnnotation type="box" text="long(&quot;driver_id&quot;).references(DriverTable.id)" label="a foreign key, and nothing else" :at="1">

```kotlin no-compile
val format = Json { prettyPrint = true }

object TaxiTable : LongIdTable("taxi") {
    val makeAndModel = json<MakeAndModel>("make_and_model", format)
    val carColor = varchar("car_color", 50)
    val driverId = long("driver_id").references(DriverTable.id)
}
```

</DrawnAnnotation>

<!--
- This is the file the demo project compiles, in full
- Click: driverId is a Long column with a foreign key to DriverTable.id
  - The types have to line up, and the compiler checks that for me
- Say it out loud: this is a reference, not a relationship
  - No @ManyToOne, no fetch type, no cascade, no orphanRemoval
  - Nothing here decides how many queries run later - that decision belongs to the query
- Call back to the N+1 slide: there is no lazy loading to be surprised by, because there is no object graph
- Handover: third table, and it is the one with the awkward types -> money and enums
-->

---
class: code-slide
---

# Money is two columns, and the column type says so

<DrawnAnnotation type="box" text="compositeMoney(\n        amountColumn = decimal(&quot;amount&quot;, 13, 2),\n        currencyColumn = varchar(&quot;currency&quot;, 3)\n    )" label="one `val`, two real columns" :at="1">

```kotlin no-compile
object TaxiRideTable : LongIdTable("taxi_ride") {
    val price = compositeMoney(
        amountColumn = decimal("amount", 13, 2),
        currencyColumn = varchar("currency", 3)
    )
}
```

</DrawnAnnotation>

<!--
- exposed-money, another opt-in module - JSR 354 MonetaryAmount, the thing you actually want for prices
- Click: one Kotlin val, two columns in the database
  - amount is a decimal(13, 2) - I chose the precision and the scale, right here
  - currency is a varchar(3) - the currency code, three characters, because that is what ISO gives me
- Say it out loud: nobody ever stored money in a double on purpose, it just happened
  - Here the type forces the pair; I cannot accidentally persist an amount without its currency
- What not to explain twice: this is the same "columns are vals" idea, just a val that makes two of them
- Handover: the currency column stores a string, my code wants a CurrencyUnit -> transform
-->

---
class: code-slide
magicMove: true
---

# A column can convert on the way in and out

<DrawnAnnotation type="box" text="transform(\n                wrap = Monetary::getCurrency,\n                unwrap = CurrencyUnit::getCurrencyCode\n            )" label="`String` in the DB, `CurrencyUnit` in my code" :at="1">

```kotlin no-compile
object TaxiRideTable : LongIdTable("taxi_ride") {
    val price = compositeMoney(
        amountColumn = decimal("amount", 13, 2),
        currencyColumn = varchar("currency", 3)
            .transform(
                wrap = Monetary::getCurrency,
                unwrap = CurrencyUnit::getCurrencyCode
            )
    )
}
```

</DrawnAnnotation>

<!--
- Click: transform takes the two functions, one each way - wrap on read, unwrap on write
  - Two method references, nothing else; the conversion is visible on the column that needs it
- In JPA this is an AttributeConverter in another file, registered somewhere, applying to who knows what
  - Here the conversion belongs to this column and to no other
- Point at the read type: price is a MonetaryAmount to my code, two boring columns to the database
- Handover: one column left, and it is the one everybody gets wrong -> the enum
-->

---
class: code-slide
magicMove: true
---

# An enum column has a default, and the default is Kotlin

<DrawnAnnotation type="underline" text="enumeration&lt;RideStatus&gt;(&quot;status&quot;).default(RideStatus.REQUESTED)" label="my enum, my default value" :at="1">

```kotlin no-compile
object TaxiRideTable : LongIdTable("taxi_ride") {
    val passengerId = reference("passenger_id", PassengerTable)
    val taxiId = reference("taxi_id", TaxiTable)

    val pickupLocation = text("pickup_location")
    val price = compositeMoney(
        amountColumn = decimal("amount", 13, 2),
        currencyColumn = varchar("currency", 3)
            .transform(
                wrap = Monetary::getCurrency,
                unwrap = CurrencyUnit::getCurrencyCode
            )
    )

    val status = enumeration<RideStatus>("status").default(RideStatus.REQUESTED)
}
```

</DrawnAnnotation>

<!--
- Now the whole file the demo project compiles - references, money, enum
- Click: enumeration of my own Kotlin enum, plus a default written as a Kotlin value
  - default takes RideStatus.REQUESTED, not the string "REQUESTED" - the compiler checks it
  - enumerationByName if I want the name in the column instead of the ordinal; my call, on this line
- Ask the room: who has been bitten by an enum stored as an ordinal after someone reordered the enum?
  - The point is not that Exposed prevents it - the point is the choice is on the line I am reading
- Say it: every decision this table makes is on screen; there is no annotation processor completing my thoughts
- Handover: three tables, all of them just values -> now let me use them to write queries, SQL DSL
-->


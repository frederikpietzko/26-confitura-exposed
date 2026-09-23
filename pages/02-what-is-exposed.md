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
- Left: domain logic stays mine - no annotations, no fetch-types, just classes
- Right: the database stays the database - schemas, tables, rows, SQL
- Middle: Exposed is a library, not a runtime that owns my objects
  - Two ways to talk to the driver: JDBC today, R2DBC for coroutines
  - Two ways to write queries: the Query DSL, or the DAO if you want something closer to an ORM
  - Integrations: Spring Boot is the one we will use, Ktor exists too (you can also just use it plain)
- nothing here translates my object graph into SQL behind my back
  - What you see is what runs, both directions
-->

---
class: module-slide
---

# Exposed Modules

<ModuleList>
  <ModuleRow name="exposed-core" tag="required">Type-safe SQL DSL</ModuleRow>
  <ModuleRow name="exposed-dao" tag="optional">Entity API on top of the DSL</ModuleRow>
  <ModuleRow name="exposed-jdbc" tag="need this or r2dbc">for blocking drivers</ModuleRow>
  <ModuleRow name="exposed-r2dbc" tag="need this or jdbc">for non-blocking drivers, coroutines</ModuleRow>
  <ModuleRow name="exposed-spring-boot4-starter">Datasource and transactions wired up</ModuleRow>
  <ModuleRow name="exposed-java-time, -json, -money" muted>Column types you opt into</ModuleRow>
</ModuleList>

<!--
- exposed-dao is the one people expect to be mandatory - it is not
  - We will not use it today, the DSL section is the talk
- jdbc versus r2dbc -> for blocking versus non-blocking drivers
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
- define Tables as kotlin objects that inherit from `Table`
- click
- table name is a string argument I pass (default is the object name)
- this object is a description of a table, not a class to use in your domain logic
  - My domain classes stay plain classes
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
    val id: Column<Long> = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
}
```

</DrawnAnnotation>

<!--
- The id is a val with a type: a Long column named id, auto incremented
- click
- I can create a primary key on any column, composite keys are also supported
-->

---
class: code-slide
magicMove: true
---

# A column carries its SQL type

<DrawnAnnotation type="underline" text="varchar(&quot;first_name&quot;, 50)" label="column definitions mirror database types" :at="1">

```kotlin no-compile
object DriverTable : Table("driver") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)

    val firstName: Column<String> = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
}
```

</DrawnAnnotation>

<!--
- columns mirror database types
- varchar with a length, because the database column has a length
  - In JPA that would be @Column(length = 50) and only if I remembered to write it
- it's the same for all other column types
- Point at the types: firstName is a Column<String>, id is a Column<Long>
  - The table object is a value I can pass around, not a mapping the framework reads
- Ask the room: who has ever been surprised by the DDL their entities generated?
- this is explicit and predictable
-->

---
class: code-slide
---

# Utility base class with an ID

<DrawnAnnotation type="circle" text="LongIdTable(&quot;taxi&quot;)" label="id and primary key included" :at="1">

```kotlin no-compile
object TaxiTable : LongIdTable("taxi")
```

</DrawnAnnotation>

<!--
- LongIdTable provides an id property with a long column type
  - there are also e.g. UUIDTable, IntIdTable, the same idea with another id type
- You can also define your own base classes if you want to
-->


---
class: code-slide
---

# Utility base class with an ID

<DrawnAnnotation type="underline" text="IdTable(&quot;taxi&quot;)" label="for tables that have an id that you want to define" :at="1">

```kotlin no-compile
object TaxiTable : IdTable("taxi") {
    override val id = long("id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(id)
}
```

</DrawnAnnotation>

<!--
- LongIdTable provides an id property with a long column type
  - there are also e.g. UUIDTable, IntIdTable, the same idea with another id type
- You can also define your own base classes if you want to
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
- click
- column type is json, the Kotlin type is my own data class
  - I'm using kotlinx.serialization here
  - but jackson works as well (or any other serializer, the serialization format is user defined)
-->

---
class: code-slide
magicMove: true
---

# A foreign key points at a Kotlin object

<DrawnAnnotation type="box" text='reference("driver_id", DriverTable)' label="a foreign key, and nothing else" :at="1">

```kotlin no-compile
val format = Json { prettyPrint = true }

object TaxiTable : LongIdTable("taxi") {
    val makeAndModel = json<MakeAndModel>("make_and_model", format)
    val carColor = varchar("car_color", 50)
    val driverId: Column<EntityID<Long>> = reference("driver_id", DriverTable)
}
```

</DrawnAnnotation>

<!--
- driverId is a foreign key pointing to the DriverTable's primary key
- no automatic fetching or lazy loading
- Handover: next I want to show a complex type: Money
-->

---
class: code-slide
---

# exposed-money integrates with javax.money

<DrawnAnnotation type="box" text="compositeMoney(\n            amountColumn = decimal(&quot;amount&quot;, 13, 2),\n            currencyColumn = varchar(&quot;currency&quot;, 3)\n        )" label="one `val`, two real columns" :at="1">

```kotlin no-compile
object TaxiRideTable : LongIdTable("taxi_ride") {
    val price: CompositeMoneyColumn<BigDecimal, CurrencyUnit, MonetaryAmount> = 
        compositeMoney(
            amountColumn = decimal("amount", 13, 2),
            currencyColumn = varchar("currency", 3)
        )
}
```

</DrawnAnnotation>

<!--
- exposed-money, another opt-in module - JSR 354 MonetaryAmount, the thing you actually want for prices
- click
- Click: one Kotlin val, two columns in the database
  - amount is a decimal(13, 2) with precision and scale
  - currency is a varchar(3) - the currency code
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
- transform takes the two functions, one each way - wrap on read, unwrap on write
- In JPA this is an AttributeConverter in another file, registered somewhere, applying to who knows what
- Here the conversion belongs to this column and to no other
- Point at the read type: price is a MonetaryAmount to my code, two boring columns to the database
-->

---
class: code-slide
magicMove: true
---

# An enum column has a default, and the default is Kotlin

<DrawnAnnotation type="underline" text="enumeration&lt;RideStatus&gt;(&quot;status&quot;).default(RideStatus.REQUESTED)" label="enums stored as strings with dbdefault" :at="1">

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
- enums stored as strings
- and of corse you can set a database default
-->
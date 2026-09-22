---
layout: intro
class: section-intro
---

# Future Plans

---
class: code-slide
---

# The DAO is coming to R2DBC

<DrawnAnnotation type="underline" text="suspend fun getAllTaxis(): List<Taxi> =" label="today: DSL only" :geometry="{ label: { x: 0.5, y: 0.56 } }" :on="1">
<DrawnAnnotation type="circle" text="TaxiEntity.all().toList()" label="EXPOSED-778: entities, suspending" :geometry="{ label: { x: 0.5, y: 0.56 } }" :at="2">

```kotlin no-compile
// R2DBC today
suspend fun getAllTaxis(): List<Taxi> =
    TaxiTable.selectAll().map { it.toTaxi() }

// EXPOSED-778, DAO 2.0
suspend fun getAllTaxis(): List<TaxiEntity> =
    TaxiEntity.all().toList()
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Exposed is already non-blocking: there is an R2DBC module and the DSL works on it
- Click one: what you write today - the DSL, suspending, no entities
- Click two: DAO 2.0 rebuilds the entity API so it can suspend too
- Say it: the DSL is the part I use anyway, so this is a gap, not a blocker
- Handover: entities need a transaction, and Spring has to know about it -> R2DBC transactions
-->

---
class: code-slide
---

# `@Transactional` will work on R2DBC too

<DrawnAnnotation type="box" text="@Transactional" label="same annotation" :geometry="{ label: { x: 0.68, y: 0.24 } }" :on="1">
<DrawnAnnotation type="underline" text="suspend fun getAllTaxis(): List<TaxiEntity> =" label="EXPOSED-818: reactive transaction manager" :geometry="{ label: { x: 0.5, y: 0.6 } }" :at="2">

```kotlin no-compile
// EXPOSED-818
@Service
@Transactional
class TaxiService {
    suspend fun getAllTaxis(): List<TaxiEntity> =
        TaxiEntity.all().toList()
}
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- The JDBC story from two sections ago, on the reactive stack
- Click one: nothing changes in my code - the annotation stays Spring's
- Click two: what is missing is a SpringTransactionManager and the autoconfiguration for R2DBC
- Until then I open the suspend transaction myself, which is exactly the boilerplate I dislike
- Handover: those two are about runtime - the next two are about my tooling -> generating tables
-->

---
class: code-slide
---

# The plugin will also run the other way round

<DrawnAnnotation type="circle" text="./gradlew generateTables" label="EXPOSED-1039: schema first" :geometry="{ label: { x: 0.74, y: 0.285 } }" :on="1">
<DrawnAnnotation type="box" text="object DriverTable : Table(&quot;driver&quot;) {" label="the table object, written for me" :geometry="{ label: { x: 0.5, y: 0.76 } }" :at="2">

```bash
# EXPOSED-1039
./gradlew generateTables
```

```kotlin no-compile
object DriverTable : Table("driver") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
}
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- The demo went tables first, because that is what the plugin supports today
- Click one: the ticket is the opposite direction - point it at a database you already have
- Click two: out comes the table object, and from there everything in this talk applies
- This is the one that makes Exposed realistic for a legacy schema I did not design
- Handover: and the last one is about how I call all of this -> repositories
-->

---
class: code-slide
---

# And a Spring Data flavour for the repository people

<DrawnAnnotation type="underline" text="ExposedRepository<Driver, Long>" label="EXPOSED-1045: spring-data-exposed" :geometry="{ label: { x: 0.5, y: 0.47 } }" :on="1">
<DrawnAnnotation type="circle" text="fun findAllByLastName(lastName: String): List<Driver>" label="derived query, typed table underneath" :geometry="{ label: { x: 0.5, y: 0.47 } }" :at="2">

```kotlin no-compile
// EXPOSED-1045
interface DriverRepository : ExposedRepository<Driver, Long> {
    fun findAllByLastName(lastName: String): List<Driver>
}
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Click one: the familiar Spring Data shape, backed by Exposed instead of JPA
- Click two: derived queries again - convenient, and exactly the magic I spent this talk avoiding
- Be honest: I would still drop into the DSL for anything interesting, and that is the point
  - the escape hatch is a normal function call, not a native query string
- Say it: the project is moving, the tickets are public, comments and votes are welcome
- Handover: and the proposal I like most is the one that replaces the method name -> query DSL
-->

---
class: code-slide
---

# Custom queries stay a DSL, not a method name

<DrawnAnnotation type="box" text="DriverTable.selectAll()" label="the same DSL as in the rest of the talk" :geometry="{ label: { x: 0.5, y: 0.5 } }" :on="1">

```kotlin no-compile
interface DriverRepository : ExposedRepository<Driver, Long> {
    fun findAllByLastName(lastName: String) = query {
        DriverTable.selectAll()
            .where { DriverTable.lastName eq lastName }
    }
}
```

</DrawnAnnotation>

<div class="mt-10 text-2xl">
<v-clicks at="2">

- implementation generated at runtime
- or at compile time via KSP

</v-clicks>
</div>

<!--
- The proposal on top of spring-data-exposed: keep the repository, drop the name parsing
- Click one: the body is the query - what you see is still what runs
- Click two and three: two ways to wire it up - a runtime proxy, or generated code via KSP
  - KSP is the one I want: the generated query is a file I can open and read
- Say it: this is the escape hatch done right - no native query string, no magic method name
- Handover: that is the roadmap - wrap up and questions
-->

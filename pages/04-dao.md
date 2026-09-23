---
layout: intro
class: section-intro
---

# DAO

## When I do want the row to be an object

---
class: code-slide
---

# An entity is a class whose properties are columns

<DrawnAnnotation type="underline" text="var carColor by TaxiTable.carColor" label="the column is the delegate" :geometry="{ label: { x: 0.5, y: 0.62 } }" :on="1">
<DrawnAnnotation type="underline" text="var driver by DriverEntity referencedOn TaxiTable.driverId" label="the relationship is one line" :geometry="{ label: { x: 0.5, y: 0.62 } }" :at="2">

```kotlin no-compile
class TaxiEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TaxiEntity>(TaxiTable)

    var makeAndModel by TaxiTable.makeAndModel
    var carColor by TaxiTable.carColor
    var driver by DriverEntity referencedOn TaxiTable.driverId
}
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Exposed has a DAO layer too, and it is built on the same tables
- Click one: property delegation - no annotation, the column itself is the delegate
- Click two: referencedOn is the foreign key I already declared, nothing new to configure
- the table stays the single source of truth, the entity is a view on it
- Warning: accessing driver will lazy load by default (you can preload it at the query-site) and needs transaction in scope (but there are ways around that)
- Handover: the companion object is the API -> usage
-->

---
class: code-slide
---

# The companion object is my CRUD API

<DrawnAnnotation type="circle" text="TaxiEntity.new" label="insert" :geometry="{ label: { x: 0.62, y: 0.19 } }" :on="1">
<DrawnAnnotation type="underline" text="TaxiEntity.all().toList()" label="read, no repository needed" :geometry="{ label: { x: 0.5, y: 0.72 } }" :at="2">

```kotlin no-compile
TaxiEntity.new {
    this.makeAndModel = makeAndModel
    this.carColor = carColor
    this.driver = driverService.getDriver(driverId)
}

TaxiEntity.findById(id)
TaxiEntity.all().toList()
TaxiEntity.findById(id)?.delete()
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- DAO's give you CRID
- Click one: new takes a lambda and gives me the entity back, id included
- Click two: findById, all and delete come from the companion - I write no repository at all
- if you happen to know the active record pattern, this is pretty close
- Note the driver assignment: I hand it an entity, not an id
- Handover: and an update is not even a call -> assignment
-->

---
class: code-slide
---

# An update is a property assignment

<DrawnAnnotation type="underline" text="taxi.carColor = carColor" occurrence="1" label="tracked, flushed on commit" :geometry="{ label: { x: 0.68, y: 0.19 } }" :at="1">

```kotlin no-compile
taxi.carColor = carColor

TaxiEntity.findByIdAndUpdate(id) { taxi ->
    taxi.carColor = carColor
}
```

```sql
UPDATE taxi SET car_color = ? WHERE taxi.id = ?
```

</DrawnAnnotation>

<!--
- Click: tradeoff -> dirty checking is back
- Difference: I opted into it here, per class, and the DSL is still right next door
- same library, two styles - you can choose and mix them
-->

---
class: code-slide
---

# Custom queries can live on the entity itself (but don't have to)

<DrawnAnnotation type="box" text="wrapRows(" label="rows in, entities out" :geometry="{ label: { x: 0.8, y: 0.28 } }" :on="1">
<DrawnAnnotation type="underline" text="DriverEntity.findAllWithoutActiveRides()" label="one call, my own SQL" :geometry="{ label: { x: 0.5, y: 0.85 } }" :at="2">

```kotlin no-compile
class DriverEntity(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, DriverEntity>(DriverTable) {
        fun findAllWithoutActiveRides() = wrapRows(
            DriverTable.selectAll().where {
                DriverTable.id notInSubQuery driverIdsWithActiveRides()
            }.orderBy(DriverTable.lastName)
        ).toList()
    }
}
```

```kotlin no-compile
val free = DriverEntity.findAllWithoutActiveRides()
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Same subselect as in the DSL section, now living on the entity
- Click one: wrapRows turns a plain DSL query into entities - no new API to learn
- Click two: callers get one named function, the query stays in one place
-->

---
class: verdict-slide
---

# DSL vs. DAO

<div class="verdict">
  <div class="verdict-card" v-click>
    <h3>DSL</h3>
    <p>I am writing the query</p>
  </div>
  <div class="verdict-card" v-click>
    <h3>DAO</h3>
    <p>CRUD without writing it</p>
  </div>
</div>

<div class="verdict-footer" v-click>Same tables underneath - pick per use case</div>

<!--
- I prefer the DSL
- Click one: DSL fits my mental model, I think in queries and tables rather than entities
- Click two: DAO offers CRUD, and a more ORM like approach and you can continue to write Queries using the DSL
- Click three: they are not mutually exclusive, both sit on the same table objects
- Mixing them is absolutely possible
-->
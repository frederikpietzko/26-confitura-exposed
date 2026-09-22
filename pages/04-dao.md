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
- Say it: the table stays the single source of truth, the entity is a view on it
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
- These are the bodies of four TaxiService methods, nothing removed but the signatures
- Click one: new takes a lambda and gives me the entity back, id included
- Click two: findById, all and delete come from the companion - I write no repository at all
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
- Two service bodies again - with an entity in hand, and with only an id
- Click: this is the trade I made in section one, dirty tracking is back
- Difference: I opted into it here, per class, and the DSL is still right next door
- Say it out loud: same library, two styles - choose per use case, not per project
- Handover: and the DAO is not a dead end -> custom queries
-->

---
class: code-slide
---

# Custom queries live on the entity itself

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
- This is the active record pattern: the query and the data live on the same class
- Handover: that is the whole DAO detour - now Spring owns the transaction
-->

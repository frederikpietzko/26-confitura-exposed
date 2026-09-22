package com.github.frederikpietzko.demo.taxi.dao

import com.github.frederikpietzko.demo.taxi.tables.TaxiTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class TaxiEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TaxiEntity>(TaxiTable)

    var makeAndModel by TaxiTable.makeAndModel
    var carColor by TaxiTable.carColor
    var driver by DriverEntity referencedOn TaxiTable.driverId
}
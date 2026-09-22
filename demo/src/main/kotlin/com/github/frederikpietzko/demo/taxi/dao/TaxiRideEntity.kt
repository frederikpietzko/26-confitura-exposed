package com.github.frederikpietzko.demo.taxi.dao

import com.github.frederikpietzko.demo.taxi.tables.TaxiRideTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class TaxiRideEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TaxiRideEntity>(TaxiRideTable)

    var passenger by PassengerEntity referencedOn TaxiRideTable.passengerId
    var taxi by TaxiEntity referencedOn TaxiRideTable.taxiId
    var pickupLocation by TaxiRideTable.pickupLocation
    var price by TaxiRideTable.price
    var status by TaxiRideTable.status
}
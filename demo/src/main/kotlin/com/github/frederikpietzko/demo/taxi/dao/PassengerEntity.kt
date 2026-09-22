package com.github.frederikpietzko.demo.taxi.dao

import com.github.frederikpietzko.demo.taxi.tables.PassengerTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class PassengerEntity(
    id: EntityID<Long>
) : LongEntity(id) {
    companion object : LongEntityClass<PassengerEntity>(PassengerTable)

    var name by PassengerTable.name
    var email by PassengerTable.email
}
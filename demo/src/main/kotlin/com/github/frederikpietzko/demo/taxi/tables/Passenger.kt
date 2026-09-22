package com.github.frederikpietzko.demo.taxi.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object PassengerTable : LongIdTable("passenger") {
    val name = text("name")
    val email = text("email").uniqueIndex()
}
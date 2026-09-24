package com.github.frederikpietzko.demo.taxi.tables

import org.jetbrains.exposed.v1.core.dao.id.IdTable

object DriverTable : IdTable<Long>("driver") {
    override val id = long("id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(id)

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
}

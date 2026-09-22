package com.github.frederikpietzko.demo.taxi.tables

import org.jetbrains.exposed.v1.core.Table

// #region driver-table
object DriverTable : Table("driver") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
}
// #endregion driver-table
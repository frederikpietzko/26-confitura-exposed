package com.github.frederikpietzko.demo.taxi.tables

import com.github.frederikpietzko.demo.taxi.domain.MakeAndModel
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.json.json


val format = Json { prettyPrint = true }

object TaxiTable : LongIdTable("taxi") {
    val makeAndModel = json<MakeAndModel>("make_and_model", format)
    val carColor = varchar("car_color", 50)
    val driverId = long("driver_id").references(DriverTable.id)
}


package com.github.frederikpietzko.demo.taxi.dao

import com.github.frederikpietzko.demo.taxi.domain.RideStatus
import com.github.frederikpietzko.demo.taxi.tables.DriverTable
import com.github.frederikpietzko.demo.taxi.tables.TaxiRideTable
import com.github.frederikpietzko.demo.taxi.tables.TaxiTable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.notInSubQuery
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

class DriverEntity(
    id: EntityID<Long>
) : Entity<Long>(id) {
    companion object : EntityClass<Long, DriverEntity>(DriverTable) {
        fun findAllWithoutActiveRides(): List<DriverEntity> = wrapRows(
            DriverTable
                .selectAll()
                .where { DriverTable.id notInSubQuery driverIdsWithActiveRides() }
                .orderBy(DriverTable.lastName)
        ).toList()

        private fun driverIdsWithActiveRides() = TaxiRideTable
            .join(TaxiTable, JoinType.INNER, TaxiRideTable.taxiId, TaxiTable.id)
            .select(TaxiTable.driverId)
            .where { TaxiRideTable.status inList listOf(RideStatus.REQUESTED, RideStatus.IN_PROGRESS) }
    }

    var firstName by DriverTable.firstName
    var lastName by DriverTable.lastName
}
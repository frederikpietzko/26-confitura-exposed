package com.github.frederikpietzko.demo.taxi.repository

import com.github.frederikpietzko.demo.taxi.domain.Driver
import com.github.frederikpietzko.demo.taxi.domain.RideStatus
import com.github.frederikpietzko.demo.taxi.tables.DriverTable
import com.github.frederikpietzko.demo.taxi.tables.TaxiRideTable
import com.github.frederikpietzko.demo.taxi.tables.TaxiTable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.notInSubQuery
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsertReturning
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class DriverRepository : BaseRepository<Driver> {
    override fun findAll(): List<Driver> =
        DriverTable.selectAll()
            .orderBy(DriverTable.lastName)
            .map(ResultRow::toDriver)

    /**
     * Drivers that are currently free: no ride of theirs is [RideStatus.REQUESTED] or
     * [RideStatus.IN_PROGRESS]. Implemented with a `NOT IN (SELECT ...)` subselect.
     */
    fun findAllWithoutActiveRides(): List<Driver> = DriverTable
        .selectAll()
        .where { DriverTable.id notInSubQuery driverIdsWithActiveRides() }
        .orderBy(DriverTable.lastName)
        .map(ResultRow::toDriver)

    private fun driverIdsWithActiveRides() = TaxiRideTable
        .join(TaxiTable, JoinType.INNER, TaxiRideTable.taxiId, TaxiTable.id)
        .select(TaxiTable.driverId)
        .where { TaxiRideTable.status inList listOf(RideStatus.REQUESTED, RideStatus.IN_PROGRESS) }

    override fun findById(id: Long): Driver? = DriverTable
        .selectAll()
        .where { DriverTable.id eq id }
        .map { it.toDriver() }
        .firstOrNull()

    override fun create(entity: Driver): Driver = DriverTable
        .insertReturning { statement ->
            statement[firstName] = entity.firstName
            statement[lastName] = entity.lastName
        }.first().toDriver()

    override fun update(entity: Driver): Driver {
        requireNotNull(entity.id) { "`id` must not be null" }
        DriverTable
            .update(
                where = { DriverTable.id eq entity.id }
            ) { statement ->
                statement[id] = entity.id
                statement[firstName] = entity.firstName
                statement[lastName] = entity.lastName
            }

        return entity
    }

    override fun upsert(entity: Driver): Driver = DriverTable
        .upsertReturning(DriverTable.id) { statement ->
            if (entity.id != null) {
                statement[id] = entity.id
            }
            statement[firstName] = entity.firstName
            statement[lastName] = entity.lastName
        }.map(ResultRow::toDriver).first()

    override fun delete(entity: Driver): Boolean {
        requireNotNull(entity.id) { "`id` must not be null" }
        return DriverTable.deleteWhere { id eq entity.id } > 0
    }

    override fun deleteById(id: Long): Boolean {
        return DriverTable.deleteWhere { DriverTable.id eq id } > 0
    }
}

private fun ResultRow.toDriver() = Driver(
    id = this[DriverTable.id],
    firstName = this[DriverTable.firstName],
    lastName = this[DriverTable.lastName],
)

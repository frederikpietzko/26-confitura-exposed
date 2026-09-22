package com.github.frederikpietzko.demo.taxi.repository

import com.github.frederikpietzko.demo.taxi.domain.Driver
import com.github.frederikpietzko.demo.taxi.tables.DriverTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class DriverRepository : BaseRepository<Driver> {
    override fun findAll(): List<Driver> =
        DriverTable.selectAll()
            .orderBy(DriverTable.lastName)
            .map(ResultRow::toDriver)

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

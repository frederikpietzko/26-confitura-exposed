package com.github.frederikpietzko.demo.taxi.repository

import com.github.frederikpietzko.demo.taxi.domain.Passenger
import com.github.frederikpietzko.demo.taxi.domain.Taxi
import com.github.frederikpietzko.demo.taxi.domain.TaxiRide
import com.github.frederikpietzko.demo.taxi.tables.PassengerTable
import com.github.frederikpietzko.demo.taxi.tables.TaxiRideTable
import com.github.frederikpietzko.demo.taxi.tables.TaxiTable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.jdbc.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
@Suppress("TooManyFunctions")
class TaxiRideRepository : BaseRepository<TaxiRide> {
    override fun findAll(): List<TaxiRide> = taxiRides().map(ResultRow::toTaxiRide)

    override fun findById(id: Long): TaxiRide? = taxiRides()
        .where { TaxiRideTable.id eq id }
        .firstOrNull()
        ?.toTaxiRide()

    override fun create(entity: TaxiRide): TaxiRide {
        val taxiId = TaxiTable.insertAndGetId { it.updateTaxi(entity.taxi) }.value

        val passengerId = PassengerTable.insertAndGetId { it.updatePassenger(entity.passenger) }.value

        val taxiRideId = TaxiRideTable.insertAndGetId {
            it.updateRide(
                ride = entity,
                taxiId = taxiId,
                passengerId = passengerId
            )
        }.value

        return entity.copy(
            id = taxiRideId,
            taxi = entity.taxi.copy(id = taxiId),
            passenger = entity.passenger.copy(id = passengerId)
        )
    }

    override fun update(entity: TaxiRide): TaxiRide {
        TaxiTable.update(
            { TaxiTable.id eq entity.taxi.id }
        ) { it.updateTaxi(entity.taxi) }
        PassengerTable.update(
            { PassengerTable.id eq entity.passenger.id }
        ) { it.updatePassenger(entity.passenger) }
        TaxiRideTable.update(
            { TaxiRideTable.id eq entity.id }
        ) { it.updateRide(entity, entity.taxi.id!!, entity.passenger.id!!) }
        return entity
    }

    override fun upsert(entity: TaxiRide): TaxiRide {
        val taxiId = TaxiTable.upsertReturning(TaxiTable.id, returning = listOf(TaxiTable.id)) {
            it.updateTaxi(entity.taxi)
        }.first()[TaxiTable.id].value
        val passengerId = PassengerTable.upsertReturning(PassengerTable.id, returning = listOf(PassengerTable.id)) {
            it.updatePassenger(entity.passenger)
        }.first()[PassengerTable.id].value

        val taxiRideId = TaxiRideTable
            .upsertReturning(TaxiRideTable.id, returning = listOf(TaxiRideTable.id)) {
                it.updateRide(entity, taxiId, passengerId)
            }.first()[TaxiRideTable.id].value

        return entity.copy(
            id = taxiRideId,
            taxi = entity.taxi.copy(id = taxiId),
            passenger = entity.passenger.copy(id = passengerId)
        )
    }

    override fun delete(entity: TaxiRide): Boolean {
        return TaxiRideTable.deleteWhere { TaxiRideTable.id eq entity.id } > 0
    }

    override fun deleteById(id: Long): Boolean {
        return TaxiRideTable.deleteWhere { TaxiRideTable.id eq id } > 0
    }

    private fun taxiRides(): Query =
        TaxiRideTable
            .join(TaxiTable, JoinType.INNER, onColumn = TaxiRideTable.taxiId, otherColumn = TaxiTable.id)
            .join(
                PassengerTable,
                JoinType.INNER,
                onColumn = TaxiRideTable.passengerId,
                otherColumn = PassengerTable.id
            )
            .selectAll()

    context(table: TaxiTable)
    private fun UpdateBuilder<*>.updateTaxi(taxi: Taxi) {
        if (taxi.id != null) {
            this[table.id] = taxi.id
        }
        this[table.makeAndModel] = taxi.makeAndModel
        this[table.carColor] = taxi.carColor
        this[table.driverId] = taxi.driverId
    }

    context(table: PassengerTable)
    private fun UpdateBuilder<*>.updatePassenger(passenger: Passenger) {
        if (passenger.id != null) {
            this[table.id] = passenger.id
        }
        this[table.name] = passenger.name
        this[table.email] = passenger.email
    }

    context(table: TaxiRideTable)
    private fun UpdateBuilder<*>.updateRide(ride: TaxiRide, taxiId: Long, passengerId: Long) {
        if (ride.id != null) {
            this[table.id] = ride.id
        }
        this[table.status] = ride.status
        this[table.price] = ride.price
        this[table.pickupLocation] = ride.pickupLocation
        this[table.taxiId] = taxiId
        this[table.passengerId] = passengerId
    }
}

private fun ResultRow.toTaxi() = Taxi(
    id = this[TaxiTable.id].value,
    makeAndModel = this[TaxiTable.makeAndModel],
    carColor = this[TaxiTable.carColor],
    driverId = this[TaxiTable.driverId],
)

private fun ResultRow.toPassenger() = Passenger(
    id = this[PassengerTable.id].value,
    name = this[PassengerTable.name],
    email = this[PassengerTable.email],
)

private fun ResultRow.toTaxiRide() = TaxiRide(
    id = this[TaxiRideTable.id].value,
    status = this[TaxiRideTable.status],
    price = this[TaxiRideTable.price],
    pickupLocation = this[TaxiRideTable.pickupLocation],
    taxi = this.toTaxi(),
    passenger = this.toPassenger(),
)

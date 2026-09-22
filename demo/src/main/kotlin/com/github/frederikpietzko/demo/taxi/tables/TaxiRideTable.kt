package com.github.frederikpietzko.demo.taxi.tables

import com.github.frederikpietzko.demo.taxi.domain.RideStatus
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.money.compositeMoney
import javax.money.CurrencyUnit
import javax.money.Monetary

object TaxiRideTable : LongIdTable("taxi_ride") {
    val passengerId = reference("passenger_id", PassengerTable)
    val taxiId = reference("taxi_id", TaxiTable)

    val pickupLocation = text("pickup_location")
    val price = compositeMoney(
        amountColumn = decimal("amount", 13, 2),
        currencyColumn = varchar("currency", 3)
            .transform(
                wrap = Monetary::getCurrency,
                unwrap = CurrencyUnit::getCurrencyCode
            )
    )

    val status = enumeration<RideStatus>("status").default(RideStatus.REQUESTED)
}

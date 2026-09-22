package com.github.frederikpietzko.demo.taxi.domain

import javax.money.MonetaryAmount

data class TaxiRide(
    val id: Long? = null,
    val status: RideStatus,
    val price: MonetaryAmount,
    val pickupLocation: String,
    val taxi: Taxi,
    val passenger: Passenger,
)

enum class RideStatus {
    REQUESTED,
    IN_PROGRESS,
    COMPLETED,
}

package com.github.frederikpietzko.demo.taxi.domain

import javax.money.MonetaryAmount

data class TaxiRide(
    val id: Long,
    val status: RideStatus,
    val price: MonetaryAmount,
    val pickupLocation: String,
)

enum class RideStatus {
    REQUESTED,
    IN_PROGRESS,
    COMPLETED,
}

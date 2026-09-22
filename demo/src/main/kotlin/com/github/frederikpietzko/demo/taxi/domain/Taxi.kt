package com.github.frederikpietzko.demo.taxi.domain

import kotlinx.serialization.Serializable

@Serializable
data class MakeAndModel(val make: String, val model: String)

data class Taxi(val id: Long? = null, val makeAndModel: MakeAndModel, val carColor: String, val driverId: Long)
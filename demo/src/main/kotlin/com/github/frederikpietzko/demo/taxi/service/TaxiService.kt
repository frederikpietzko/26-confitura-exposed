package com.github.frederikpietzko.demo.taxi.service

import com.github.frederikpietzko.demo.taxi.dao.TaxiEntity
import com.github.frederikpietzko.demo.taxi.domain.MakeAndModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TaxiService(
    private val driverService: DriverService,
) {
    fun create(makeAndModel: MakeAndModel, carColor: String, driverId: Long): TaxiEntity {
        return TaxiEntity.new {
            this.makeAndModel = makeAndModel
            this.carColor = carColor
            this.driver = driverService.getDriver(driverId)
        }
    }

    fun updateCarColor(taxi: TaxiEntity, carColor: String) {
        taxi.carColor = carColor
    }

    fun updateCarColor(id: Long, carColor: String) {
        TaxiEntity.findByIdAndUpdate(id) { taxi ->
            taxi.carColor = carColor
        }
    }

    fun getTaxiById(id: Long): TaxiEntity {
        return requireNotNull(TaxiEntity.findById(id)) { "Taxi with id $id not found" }
    }

    fun getAllTaxis(): List<TaxiEntity> {
        return TaxiEntity.all().toList()
    }

    fun deleteTaxiById(id: Long) {
        TaxiEntity.findById(id)?.delete()
    }
}
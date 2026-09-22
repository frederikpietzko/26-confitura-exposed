package com.github.frederikpietzko.demo.taxi.service

import com.github.frederikpietzko.demo.taxi.dao.DriverEntity
import com.github.frederikpietzko.demo.taxi.dto.CreateDriverDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DriverService {
    fun createDriver(driver: CreateDriverDto): DriverEntity {
        val driver = DriverEntity.new {
            firstName = driver.firstName
            lastName = driver.lastName
        }
        return driver
    }

    fun getDriver(id: Long): DriverEntity =
        requireNotNull(DriverEntity.findById(id)) { "Driver with id $id not found" }

    fun updateLastName(driver: DriverEntity, lastName: String): DriverEntity {
        driver.lastName = lastName
        return driver
    }

    fun updateLastName(driverId: Long, lastName: String): DriverEntity {
        return DriverEntity.findByIdAndUpdate(driverId) {
            it.lastName = lastName
        }.let { requireNotNull(it) { "Driver with id $driverId not found" } }
    }

    fun delete(driver: DriverEntity) {
        driver.delete()
    }

    fun delete(driverId: Long) {
        delete(getDriver(driverId))
    }

    fun findAllWithoutActiveRides() =
        DriverEntity.findAllWithoutActiveRides()
}
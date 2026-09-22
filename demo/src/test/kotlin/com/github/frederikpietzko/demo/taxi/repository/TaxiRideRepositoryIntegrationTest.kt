package com.github.frederikpietzko.demo.taxi.repository

import com.github.frederikpietzko.demo.taxi.domain.Driver
import com.github.frederikpietzko.demo.taxi.domain.MakeAndModel
import com.github.frederikpietzko.demo.taxi.domain.Passenger
import com.github.frederikpietzko.demo.taxi.domain.RideStatus
import com.github.frederikpietzko.demo.taxi.domain.Taxi
import com.github.frederikpietzko.demo.taxi.domain.TaxiRide
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import javax.money.Monetary
import javax.money.MonetaryAmount
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaxiRideRepositoryIntegrationTest @Autowired constructor(
    private val rides: TaxiRideRepository,
    private val drivers: DriverRepository,
) : RepositoryIntegrationTest() {

    @Test
    fun `create stores the ride together with its taxi and passenger`() {
        val created = rides.create(aRide())

        val id = assertNotNull(created.id)
        val found = assertNotNull(rides.findById(id))
        assertEquals(RideStatus.REQUESTED, found.status)
        assertEquals("Warsaw Central", found.pickupLocation)
        assertTrue(found.price.isEqualTo(euro("25.50")))
        assertEquals(MakeAndModel("Skoda", "Octavia"), found.taxi.makeAndModel)
        assertEquals(created.taxi.id, found.taxi.id)
        assertEquals("grace@example.com", found.passenger.email)
        assertEquals(created.passenger.id, found.passenger.id)
    }

    @Test
    fun `findById returns null for an unknown id`() {
        assertNull(rides.findById(4711L))
    }

    @Test
    fun `findAll returns every stored ride with its joined taxi and passenger`() {
        val first = rides.create(aRide(pickupLocation = "Warsaw Central"))
        val second = rides.create(aRide(pickupLocation = "Airport", email = "ada@example.com"))

        val found = rides.findAll()

        assertEquals(listOf(first.id, second.id), found.map { it.id })
        assertEquals(listOf("Warsaw Central", "Airport"), found.map { it.pickupLocation })
        assertEquals(listOf(first.taxi.id, second.taxi.id), found.map { it.taxi.id })
        assertEquals(listOf("grace@example.com", "ada@example.com"), found.map { it.passenger.email })
    }

    @Test
    fun `update overwrites the ride, its taxi and its passenger`() {
        val created = rides.create(aRide())

        val changed = created.copy(
            status = RideStatus.COMPLETED,
            pickupLocation = "Airport",
            price = euro("31.00"),
            taxi = created.taxi.copy(carColor = "black"),
            passenger = created.passenger.copy(name = "Grace B. Hopper"),
        )
        val returned = rides.update(changed)

        assertEquals(changed, returned)
        val found = assertNotNull(rides.findById(created.id!!))
        assertEquals(RideStatus.COMPLETED, found.status)
        assertEquals("Airport", found.pickupLocation)
        assertTrue(found.price.isEqualTo(euro("31.00")))
        assertEquals("black", found.taxi.carColor)
        assertEquals("Grace B. Hopper", found.passenger.name)
    }

    @Test
    fun `upsert inserts a ride that has no id yet`() {
        val upserted = rides.upsert(aRide())

        val id = assertNotNull(upserted.id)
        assertEquals(listOf(id), rides.findAll().map { it.id })
    }

    @Test
    fun `upsert updates the existing ride instead of adding a row`() {
        val created = rides.create(aRide())

        val upserted = rides.upsert(created.copy(status = RideStatus.IN_PROGRESS))

        assertEquals(created.id, upserted.id)
        assertEquals(listOf(RideStatus.IN_PROGRESS), rides.findAll().map { it.status })
    }

    @Test
    fun `delete removes the ride`() {
        val created = rides.create(aRide())

        assertTrue(rides.delete(created))
        assertEquals(emptyList(), rides.findAll())
    }

    @Test
    fun `delete reports no deletion for a ride without an id`() {
        assertEquals(false, rides.delete(aRide()))
    }

    @Test
    fun `deleteById removes the ride`() {
        val created = rides.create(aRide())

        assertTrue(rides.deleteById(created.id!!))
        assertNull(rides.findById(created.id))
    }

    @Test
    fun `deleteById returns false for an unknown id`() {
        assertEquals(false, rides.deleteById(4711L))
    }

    private fun aRide(
        status: RideStatus = RideStatus.REQUESTED,
        price: MonetaryAmount = euro("25.50"),
        pickupLocation: String = "Warsaw Central",
        email: String = "grace@example.com",
    ): TaxiRide {
        val driver = drivers.create(Driver(firstName = "Ada", lastName = "Lovelace"))

        return TaxiRide(
            status = status,
            price = price,
            pickupLocation = pickupLocation,
            taxi = Taxi(
                makeAndModel = MakeAndModel(make = "Skoda", model = "Octavia"),
                carColor = "yellow",
                driverId = driver.id!!,
            ),
            passenger = Passenger(name = "Grace Hopper", email = email),
        )
    }

    private fun euro(amount: String): MonetaryAmount = Monetary
        .getDefaultAmountFactory()
        .setCurrency("EUR")
        .setNumber(BigDecimal(amount))
        .create()
}

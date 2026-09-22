package com.github.frederikpietzko.demo.taxi.repository

import com.github.frederikpietzko.demo.taxi.domain.Driver
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DriverRepositoryIntegrationTest @Autowired constructor(private val drivers: DriverRepository) :
    RepositoryIntegrationTest() {

    @Test
    fun `create assigns a generated id and stores the driver`() {
        val created = drivers.create(Driver(firstName = "Ada", lastName = "Lovelace"))

        val id = assertNotNull(created.id)
        assertEquals(Driver(id = id, firstName = "Ada", lastName = "Lovelace"), drivers.findById(id))
    }

    @Test
    fun `findById returns null for an unknown id`() {
        assertNull(drivers.findById(4711L))
    }

    @Test
    fun `findAll returns every stored driver`() {
        val ada = drivers.create(Driver(firstName = "Ada", lastName = "Lovelace"))
        val alan = drivers.create(Driver(firstName = "Alan", lastName = "Turing"))

        assertEquals(listOf(ada, alan), drivers.findAll())
    }

    @Test
    fun `update overwrites the stored driver`() {
        val created = drivers.create(Driver(firstName = "Ada", lastName = "Lovelace"))
        val renamed = created.copy(lastName = "Byron")

        val returned = drivers.update(renamed)

        assertEquals(renamed, returned)
        assertEquals(renamed, drivers.findById(created.id!!))
    }

    @Test
    fun `update rejects a driver without an id`() {
        val failure = assertFailsWith<IllegalArgumentException> {
            drivers.update(Driver(firstName = "Ada", lastName = "Lovelace"))
        }

        assertEquals("`id` must not be null", failure.message)
    }

    @Test
    fun `upsert inserts a driver without an id`() {
        val upserted = drivers.upsert(Driver(firstName = "Ada", lastName = "Lovelace"))

        val id = assertNotNull(upserted.id)
        assertEquals(listOf(Driver(id = id, firstName = "Ada", lastName = "Lovelace")), drivers.findAll())
    }

    @Test
    fun `upsert updates the existing driver instead of adding a row`() {
        val created = drivers.create(Driver(firstName = "Ada", lastName = "Lovelace"))

        val upserted = drivers.upsert(created.copy(firstName = "Augusta"))

        assertEquals(created.copy(firstName = "Augusta"), upserted)
        assertEquals(listOf(created.copy(firstName = "Augusta")), drivers.findAll())
    }

    @Test
    fun `delete removes the driver`() {
        val created = drivers.create(Driver(firstName = "Ada", lastName = "Lovelace"))

        assertTrue(drivers.delete(created))
        assertEquals(emptyList(), drivers.findAll())
    }

    @Test
    fun `delete rejects a driver without an id`() {
        val failure = assertFailsWith<IllegalArgumentException> {
            drivers.delete(Driver(firstName = "Ada", lastName = "Lovelace"))
        }

        assertEquals("`id` must not be null", failure.message)
    }

    @Test
    fun `deleteById removes the driver and reports whether a row was hit`() {
        val created = drivers.create(Driver(firstName = "Ada", lastName = "Lovelace"))

        assertTrue(drivers.deleteById(created.id!!))
        assertNull(drivers.findById(created.id))
    }

    @Test
    fun `deleteById returns false for an unknown id`() {
        assertEquals(false, drivers.deleteById(4711L))
    }
}

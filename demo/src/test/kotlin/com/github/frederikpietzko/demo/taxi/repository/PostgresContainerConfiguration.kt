package com.github.frederikpietzko.demo.taxi.repository

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * The repositories rely on `INSERT ... RETURNING` and `ON CONFLICT ... RETURNING`, which Exposed
 * only implements for PostgreSQL, SQLite and MariaDB - so the tests run against the real thing.
 */
@TestConfiguration(proxyBeanMethods = false)
class PostgresContainerConfiguration {
    @Bean
    @ServiceConnection
    fun postgres(): PostgreSQLContainer = PostgreSQLContainer("postgres:18-alpine")
}

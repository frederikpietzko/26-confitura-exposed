package com.github.frederikpietzko.demo.taxi.repository

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

/**
 * Boots the real application context against a PostgreSQL container migrated by Flyway.
 * Every test runs in its own transaction which is rolled back afterwards, so each test starts
 * from an empty schema.
 */
@SpringBootTest
@Import(PostgresContainerConfiguration::class)
@Transactional
@Tag("integration")
@Suppress("AbstractClassCanBeInterface")
abstract class RepositoryIntegrationTest

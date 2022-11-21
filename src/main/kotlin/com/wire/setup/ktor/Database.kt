package com.wire.setup.ktor

import com.wire.dao.Database
import com.wire.extensions.createLogger
import com.wire.setup.configuration.DatabaseConfiguration
import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

private val logger = createLogger("DatabaseSetup")

/**
 * Connect the database.
 */
fun Application.installDatabase() {
    logger.info { "Connecting to the database." }

    logger.debug { "Connecting to the DB." }
    runCatching {
        val database by closestDI().instance<Database>()

        require(database.isConnected()) { "It was not possible to connect to the database!" }
        logger.info { "Database is connected." }

        migrateDatabase()
    }.onFailure {
        logger.error(it) { "${it.message} Exiting..." }
    }.getOrThrow()
}

// Migrate database using flyway.
private fun Application.migrateDatabase() {
    val dbConfig by closestDI().instance<DatabaseConfiguration>()
    val shouldMigrate = dbConfig.shouldMigrate
    logger.info { "Migrating database - should migrate: $shouldMigrate." }

    // enable migration by default
    if (shouldMigrate) {
        val flyway by closestDI().instance<Flyway>()
        logger.info { "Migrating database..." }

        val migrateResult = flyway.migrate()

        logger.info {
            if (migrateResult.migrationsExecuted == 0) {
                "No migrations necessary."
            } else {
                "Applied ${migrateResult.migrationsExecuted} migrations."
            }
        }
    } else {
        logger.warn { "Skipping database migration and verification, this should not be in production!" }
    }
}

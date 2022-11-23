package com.wire.setup.ktor

import com.wire.dao.Database
import com.wire.dao.model.Conversations
import com.wire.dao.model.Teams
import com.wire.dao.model.Users
import com.wire.extensions.createLogger
import com.wire.setup.configuration.DatabaseConfiguration
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.SchemaUtils
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
//        val flyway by closestDI().instance<Flyway>()
//        logger.info { "Migrating database..." }
//
//        val migrateResult = flyway.migrate()
//
//        logger.info {
//            if (migrateResult.migrationsExecuted == 0) {
//                "No migrations necessary."
//            } else {
//                "Applied ${migrateResult.migrationsExecuted} migrations."
//            }
//        }
        // TODO disable this and let Flyway do the job, once we have proper schema
        initializeDebugDatabase()
    } else {
        logger.warn { "Skipping database migration and verification, this should not be in production!" }
    }
}

private fun Application.initializeDebugDatabase() {
    logger.warn { "Dropping and recreating database! This definitely should not be in the production!" }
    val db by closestDI().instance<Database>()

    val allTables = tables()
        .map { it.nameInDatabaseCase() }

    logger.warn { "Deleting tables $allTables." }
    db.blockingQuery {
        exec("drop table if exists ${allTables.joinToString(",")};")
    }

    logger.warn { "Creating tables $allTables." }
    db.blockingQuery {
        SchemaUtils.create(*tables())
    }

    logger.warn { "Database should be clean and ready now." }
}

// we have it internal, so we can access it from the tests
internal fun tables() = arrayOf(Teams, Users, Conversations)

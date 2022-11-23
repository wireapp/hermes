package com.wire.base

import com.wire.dao.Database
import com.wire.setup.ktor.tables
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.kodein.di.DI
import org.kodein.di.instance
import javax.sql.DataSource

/**
 * Base class that has access to the database.
 *
 * The database is cleaned and migrated before each test.
 */
open class DatabaseTestBase(
    private val shouldSetupDatabase: Boolean = true
) : DiAwareTestBase() {

    @BeforeEach
    fun beforeEach() {
        if (shouldSetupDatabase) {
            // first we need to create the database
            val dataSource by instance<DataSource>("root-ds")
            val dbName by instance<String>("db-name")
            dataSource.connection.use { c ->
                c.createStatement().executeUpdate("create database $dbName;")
            }
            // and then all stuff should use data source that is already connected to this data source

            val db by instance<Database>()
            require(db.isConnected()) { "It was not possible to connect to db database!" }

            // TODO disable this and let Flyway do the job, once we have proper schema
            SchemaUtils.create(*tables())
//            val flyway by instance<Flyway>()
//            flyway.migrate()

            populateDatabase(rootDI)
        }
    }

    @AfterEach
    fun afterEach() {
        if (shouldSetupDatabase) {
            // and now tear down the database
            val dataSource by instance<DataSource>("root-ds")
            val dbName by instance<String>("db-name")
            dataSource.connection.use { c ->
                c.createStatement().executeUpdate("drop database $dbName WITH (FORCE);")
            }
        }
    }

    /**
     * Override this when you need to add additional data before each test.
     * This method is called when the database is fully migrated.
     *
     * Executed only when [shouldSetupDatabase] is true.
     */
    protected open fun populateDatabase(di: DI) {}
}

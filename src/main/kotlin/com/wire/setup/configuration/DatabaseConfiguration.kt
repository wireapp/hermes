package com.wire.setup.configuration

import com.sksamuel.hoplite.Masked

/**
 * Simple configuration for the database connection.
 */
data class DatabaseConfiguration(
    /**
     * Username for login.
     */
    val userName: String,
    /**
     * Password for login.
     */
    val password: Masked,
    /**
     * URL where the database is running.
     */
    val url: String,
    /**
     * Determines whether the application should run flyway migration or not.
     */
    val shouldMigrate: Boolean
)

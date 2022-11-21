package com.wire.setup.configuration

/**
 * Complete configuration as in application.yml.
 */
data class Configuration(
    /**
     * Configuration for the server properties such as ports.
     */
    val server: ServerConfiguration,
    /**
     * Information about the running application and the code.
     */
    val application: ApplicationInformation,
    /**
     * Database configuration.
     */
    val database: DatabaseConfiguration,
    /**
     * Swagger feature configuration.
     */
    val swagger: SwaggerConfiguration
)

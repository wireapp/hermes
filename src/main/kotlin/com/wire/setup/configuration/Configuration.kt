package com.wire.setup.configuration

import com.wire.setup.configuration.auth.JwtConfiguration

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
    val swagger: SwaggerConfiguration,
    val federation: FederationConfiguration,
    val jwt: JwtConfiguration
)

package com.wire

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.preprocessor.EnvOrSystemPropertyPreprocessor
import com.sksamuel.hoplite.preprocessor.LookupPreprocessor
import com.wire.extensions.createLogger
import com.wire.setup.configuration.Configuration
import com.wire.setup.ktor.setupForConfiguration
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

/**
 * Application startup.
 */
fun main() {
    val logger = createLogger("Application")

    logger.info { "Starting application." }
    runCatching {
        logger.debug { "Parsing configuration." }
        // as a first thing load configuration
        val cfg = ConfigLoader.builder()
            .addPreprocessor(EnvOrSystemPropertyPreprocessor)
            .addPreprocessor(LookupPreprocessor)
            .addSource(PropertySource.resource("/application.yml"))
            .build()
            .loadConfigOrThrow<Configuration>()

        logger.debug { "Creating application engine." }

        val env = applicationEngineEnvironment {
            module { setupForConfiguration(cfg) }
            // public API
            connector {
                port = cfg.server.publicApiPort
            }
        }

        logger.debug { "Starting server." }
        embeddedServer(Netty, env).start(wait = true)
    }.onFailure {
        logger.error(it) { "There was a problem during application startup." }
        // in case logger failed completely
        it.printStackTrace()
    }.getOrThrow()
}

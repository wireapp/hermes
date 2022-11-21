package com.wire.setup.ktor

import com.wire.error.installExceptionHandling
import com.wire.routing.installRouting
import com.wire.setup.configuration.Configuration
import com.wire.setup.di.registerClasses
import com.wire.setup.di.registerConfiguration
import io.ktor.server.application.Application
import org.kodein.di.ktor.di

/**
 * Loads the application.
 */
fun Application.setupForConfiguration(configuration: Configuration) {
    // setup DI
    di {
        registerConfiguration(configuration)
        registerClasses()
    }
    // now kodein is running and can be used
    setupDiAwareApplication()
}

/**
 * Application that already has DI context.
 */
fun Application.setupDiAwareApplication() {
    installBasics()

    installDatabase()

    installSwagger()
    installMonitoring()

    installRouting()
    installExceptionHandling()
}

package com.wire.setup.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

/**
 * Install basic content handling.
 */
fun Application.installBasics() {
    val objectMapper by closestDI().instance<ObjectMapper>()
    // initialize our own configuration for Jackson
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    // as we're running behind the proxy, we take remote host from X-Forwarded-From
    install(XForwardedHeaders)
}

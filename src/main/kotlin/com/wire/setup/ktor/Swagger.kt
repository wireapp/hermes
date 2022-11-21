package com.wire.setup.ktor

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import com.wire.routing.Routes
import com.wire.setup.configuration.SwaggerConfiguration
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import kotlin.reflect.KType

/**
 * Install swagger feature.
 */
fun Application.installSwagger() {
    val swaggerConfiguration by closestDI().instance<SwaggerConfiguration>()

    // install swagger
    install(OpenAPIGen) {
        api.info = swaggerConfiguration.info
        // openapi json
        openApiJsonPath = Routes.openApiJson
        serveOpenApiJson = swaggerConfiguration.enableOpenApiJson
        // UI
        swaggerUiPath = Routes.swaggerUi
        serveSwaggerUi = swaggerConfiguration.enableSwaggerUI
        // dto naming without package names
        replaceModule(DefaultSchemaNamer, object : SchemaNamer {
            override fun get(type: KType) = type
                .toString()
                .replace(Regex("[A-Za-z0-9_.]+")) { it.value.split(".").last() }
                .replace(Regex(">|<|, "), "_")
        })
    }
}

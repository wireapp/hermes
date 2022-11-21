package com.wire.routing

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.tags
import com.wire.dto.OkResponse
import com.wire.extensions.closestDI
import com.wire.setup.configuration.ApplicationInformation
import org.kodein.di.instance

/**
 * Registers service endpoints..
 */
fun NormalOpenAPIRoute.serviceRoutes() {
    val info by closestDI().instance<ApplicationInformation>()

    route(Routes.version).get<Unit, ServiceVersion>(
        info(summary = "Returns version of the application."),
        tags(SwaggerTags.service)
    ) { respond(ServiceVersion(info.version)) }

    route(Routes.status).get<Unit, OkResponse>(
        info(summary = "Always responds with HTTP status 200 if the application is up and running."),
        tags(SwaggerTags.service)
    ) { respond(OkResponse()) }

    route(Routes.statusHealth).get<Unit, ServiceHealth>(
        info(summary = "Returns description of the service health."),
        tags(SwaggerTags.service)
    ) {
        respond(ServiceHealth("healthy"))
    }
}

internal data class ServiceVersion(val version: String)
internal data class ServiceHealth(val status: String)

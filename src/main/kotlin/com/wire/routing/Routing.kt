package com.wire.routing

import com.papsign.ktor.openapigen.route.apiRouting
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Install and configure routing.
 */
fun Application.installRouting() {
    // register normal routes
    routing {
        // convenient redirect to swagger
        get("/") {
            call.respondRedirect(Routes.swaggerUi, permanent = false)
        }
    }

    // register API routing with swagger
    apiRouting {
        loginRoutes()
        featureFlagsRoutes()
        conversationRoutes()
        connectionRoutes()
        usersRoutes()
        notificationRoutes()

        serviceRoutes()
    }
}

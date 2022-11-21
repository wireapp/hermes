package com.wire.setup.ktor

import com.wire.extensions.createLogger
import com.wire.extensions.determineRealIp
import com.wire.monitoring.INSTANCE
import com.wire.monitoring.INSTANCE_ID
import com.wire.monitoring.PATH
import com.wire.monitoring.REMOTE_HOST
import com.wire.monitoring.REQUEST_ID
import com.wire.monitoring.SESSION_ID
import com.wire.routing.Routes
import dev.forst.katlib.InstantProvider
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.sessions.sessionId
import io.ktor.util.AttributeKey
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.slf4j.event.Level
import java.util.UUID

/**
 * Installs monitoring features.
 */
fun Application.installMonitoring() {
    val nowProvider by closestDI().instance<InstantProvider>()

    val callStartMillis = AttributeKey<Long>("CallStartTime")
    intercept(ApplicationCallPipeline.Setup) {
        // intercept before calling routing and mark every incoming call with a TimeMark
        call.attributes.put(callStartMillis, nowProvider.now().toEpochMilli())
    }

    // requests logging in debug mode + MDC tracing
    install(CallLogging) {
        // put useful information to log context
        mdc(REQUEST_ID) { it.callId }
        mdc(REMOTE_HOST) { it.request.determineRealIp() }
        mdc(PATH) { "${it.request.httpMethod.value} ${it.request.path()}" }
        mdc(INSTANCE_ID) { INSTANCE }
        mdc(SESSION_ID) { it.sessionId }

        val ignoredPaths = setOf<String>()
        val ignoredMethods = setOf(HttpMethod.Options, HttpMethod.Head)
        filter {
            val path = it.request.path()
            path.startsWith(Routes.apiPrefix) &&
                    !ignoredPaths.contains(path) && // without ignored service paths
                    !ignoredMethods.contains(it.request.httpMethod) // and ignored, not used, methods
        }
        level = Level.INFO // we want to log especially the results of the requests
        logger = createLogger("HttpCallLogger")

        format {
            val millis = it.attributes.getOrNull(callStartMillis)
                ?.let { startMillis -> nowProvider.now().toEpochMilli() - startMillis }
                ?: -1L

            "${it.request.determineRealIp()}: ${it.request.httpMethod.value} ${it.request.path()} -> " +
                    "$millis mls -> ${it.response.status()?.value} ${it.response.status()?.description}"
        }
    }
    // MDC call id setup
    install(CallId) {
        retrieveFromHeader("X-Request-Id")
        generate { UUID.randomUUID().toString() }
    }
}

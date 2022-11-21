package com.wire.error


import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.papsign.ktor.openapigen.exceptions.OpenAPIRequiredFieldException
import com.wire.extensions.createLogger
import com.wire.monitoring.INSTANCE
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

private val logger = createLogger("ExceptionHandler")

/**
 * Application error handling.
 */
fun Application.installExceptionHandling() {
    install(StatusPages) {
        jsonExceptions()
        // generic error handling
        exception<Exception> { call, e ->
            logger.error(e) { "Unknown exception occurred in the application: ${e.message}." }
            call.errorResponse(
                e,
                HttpStatusCode.InternalServerError,
                "Server was unable to fulfill the request, please contact administrator with request ID: ${call.callId}."
            )
        }

    }
}

private fun StatusPagesConfig.jsonExceptions() {
    val eR: suspend ApplicationCall.(Exception, String) -> Unit = { e, message ->
        errorResponse(e, HttpStatusCode.BadRequest, message)
    }

    exception<BadRequestException> { call, e ->
        logger.warn(e) { "Deserialization problem ${e.message}." }
        call.eR(e, "Bad request, incorrect payload.")
    }

    // wrong format of some property
    exception<InvalidFormatException> { call, e ->
        logger.warn(e) { "Invalid data format." }
        call.eR(e, "Wrong data format.")
    }

    // server received JSON with additional properties it does not know
    exception<UnrecognizedPropertyException> { call, e ->
        logger.warn(e) { "Unrecognized property in the JSON." }
        call.eR(e, "Unrecognized body property ${e.propertyName}.")
    }

    // missing data in the request
    exception<MissingKotlinParameterException> { call, e ->
        logger.warn(e) { "Missing parameter in the request: ${e.message}." }
        call.eR(e, "Missing parameter: ${e.parameter}.")
    }

    // generic, catch-all exception from jackson serialization
    exception<JacksonException> { call, e ->
        logger.warn(e) { "Could not deserialize data: ${e.message}." }
        call.eR(e, "Bad request, could not deserialize data.")
    }

    exception<OpenAPIRequiredFieldException> { call, e ->
        logger.warn(e) { "Could not deserialize data: ${e.message}." }
        call.eR(e, "Bad request, could not deserialize data.")
    }
}

private suspend inline fun ApplicationCall.errorResponse(
    ex: Exception,
    statusCode: HttpStatusCode,
    message: String? = null,
    errorType: ErrorType? = null,
    detail: Any? = null
) {
    if (logger.isDebugEnabled) {
        logger.debug(ex) { "There was an exception in the application: ${ex.message}." }
    }

    respond(
        status = statusCode,
        ErrorResponse(
            code = statusCode.value,
            message = message ?: "No details specified.",
            label = errorType,
            requestId = callId,
            instanceId = INSTANCE,
            detail = detail
        )
    )
}

@file:Suppress("UnusedPrivateMember", "unused") // goodies for the future

package com.wire.extensions

import com.papsign.ktor.openapigen.route.OpenAPIRoute
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import org.kodein.di.LazyDI
import org.kodein.di.ktor.closestDI
import java.io.InputStream

/**
 * Simple extension to provide DI container inside Swagger defined routes.
 */
fun OpenAPIRoute<*>.closestDI(): LazyDI = ktorRoute.application.closestDI()

/**
 * Shortcut for application DI.
 */
fun Routing.closestDI(): LazyDI = this.application.closestDI()

/**
 * Shortcut for pipeline context.
 */
val OpenAPIPipelineResponseContext<*>.context
    get() = pipeline.context

/**
 * Shortcut for pipeline request.
 */
val OpenAPIPipelineResponseContext<*>.request
    get() = context.request

/**
 * Responds only with the given status code.
 */
suspend fun OpenAPIPipelineResponseContext<*>.respondWithStatus(status: HttpStatusCode) = request.call.respond(status)

/**
 * Responds with given [status] and [message].
 */
suspend inline fun <reified T : Any> OpenAPIPipelineResponseContext<T>.respondWithStatus(
    status: HttpStatusCode,
    message: T
) = request.call.respond(status, message)

/**
 * Responds with given [status], [fileName] and [fileBytes].
 */
suspend inline fun OpenAPIPipelineResponseContext<*>.respondWithFile(
    status: HttpStatusCode,
    fileName: String,
    fileBytes: ByteArray
) {
    request.call.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, fileName)
            .toString()
    )
    request.call.respond(status, fileBytes)
}

/**
 * Responds with given [status], [fileName] and [inputStream].
 */
suspend inline fun OpenAPIPipelineResponseContext<*>.respondWithFile(
    status: HttpStatusCode,
    fileName: String?,
    inputStream: InputStream
) {
    if (fileName != null) {
        request.call.response
            .header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(ContentDisposition.Parameters.FileName, fileName)
                    .toString()
            )
    }

    request.call.respondOutputStream(status = status) {
        inputStream.buffered().use { it.copyTo(this) }
    }
}

/**
 * Responds with given [status] and [inputStream].
 */
suspend inline fun OpenAPIPipelineResponseContext<*>.respondWithStream(
    inputStream: InputStream,
    status: HttpStatusCode = HttpStatusCode.OK
) {
    // respond in non-blocking way
    request.call.respondOutputStream(
        contentType = ContentType.Application.OctetStream,
        status = status
    ) {
        inputStream.buffered().use { it.copyTo(this) }
    }
}

/**
 * Responds with redirect to given [url].
 */
suspend inline fun OpenAPIPipelineResponseContext<*>.respondRedirect(url: String, permanent: Boolean = false) {
    request.call.respondRedirect(url, permanent)
}

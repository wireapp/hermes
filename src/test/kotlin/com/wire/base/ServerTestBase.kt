package com.wire.base

import com.fasterxml.jackson.databind.ObjectMapper
import com.wire.error.installExceptionHandling
import com.wire.routing.installRouting
import com.wire.setup.ktor.installBasics
import com.wire.setup.ktor.installDatabase
import com.wire.setup.ktor.installMonitoring
import com.wire.setup.ktor.installSwagger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.testApplication
import io.ktor.server.testing.withTestApplication
import mu.KLogging
import org.kodein.di.LazyDI
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import java.nio.charset.Charset
import kotlin.test.assertEquals

/**
 * Base class with access to running Ktor server. Parameter needsDatabase indicates
 * whether the test class needs access to the database or not.
 *
 * Use [withTestApplication] to access the server resource.
 *
 * Examples available: [here](https://github.com/ktorio/ktor-documentation/tree/master/codeSnippets/snippets/testable)
 */
open class ServerTestBase(
    private val needsDatabase: Boolean = true,
) : DatabaseTestBase(needsDatabase) {

    protected companion object : KLogging()

    protected val mapper by rootDI.instance<ObjectMapper>()

    protected fun <R> runTestApplication(
        test: suspend ApplicationTestBuilder.(HttpClient) -> R
    ): Unit = testApplication {
        application {
            di {
                extend(rootDI, allowOverride = true)
            }
            // connect to the database
            if (needsDatabase) {
                installDatabase()
            }
            // configure Ktor
            installBasics()
            installSwagger()
            installMonitoring()
            installRouting()
            installExceptionHandling()
        }

        test(createClient())
    }

    protected fun ApplicationTestBuilder.createClient(
        installCookies: Boolean = false
    ): HttpClient =
        createClient {
            install(ContentNegotiation) {
                register(ContentType.Application.Json, JacksonConverter(mapper))
            }
            if (installCookies) {
                install(HttpCookies)
            }
        }

    protected fun TestApplicationEngine.closestDI(): LazyDI = application.closestDI()

    protected inline fun <reified T> HttpRequestBuilder.jsonBody(data: T) {
        setBody(data)
        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }

    protected fun HttpResponse.expectStatus(expected: HttpStatusCode) = assertEquals(expected, this.status)
    protected suspend inline fun <reified T> HttpResponse.expectJsonBody(expected: T) {
        assertEquals(
            ContentType.Application.Json.withCharset(Charset.defaultCharset()), contentType(),
            "Response content type is different than expected!"
        )
        assertEquals(expected, body(), "Body in response is different than expected!")
    }
}


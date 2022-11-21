package com.wire.routing

import com.wire.base.ServerTestBase
import com.wire.dto.OkResponse
import com.wire.setup.configuration.ApplicationInformation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.Test
import org.kodein.di.instance

class ServiceRoutesTest : ServerTestBase(needsDatabase = false) {

    @Test
    fun `test version route returns correct version`() = runTestApplication { client ->
        val info by instance<ApplicationInformation>()

        client.get(Routes.version).apply {
            expectStatus(HttpStatusCode.OK)
            expectJsonBody(ServiceVersion(info.version))
        }
    }


    @Test
    fun `test status route returns always ok`() = runTestApplication { client ->
        client.get(Routes.status).apply {
            expectStatus(HttpStatusCode.OK)
            expectJsonBody(OkResponse())
        }
    }

    @Test
    fun `test statusHealth returns healthy status`() = runTestApplication { client ->
        client.get(Routes.statusHealth).apply {
            expectStatus(HttpStatusCode.OK)
            expectJsonBody(ServiceHealth("healthy"))
        }
    }
}

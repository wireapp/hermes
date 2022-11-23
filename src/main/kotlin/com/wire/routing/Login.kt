package com.wire.routing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.wire.dao.KeyPackage
import com.wire.dao.NonQualifiedUserId
import com.wire.dao.QualifiedId
import com.wire.dto.access.AccessTokenDTO
import com.wire.dto.access.RefreshToken
import com.wire.dto.user.Password
import com.wire.dto.user.UserDTO
import com.wire.error.EntityNotFoundException
import com.wire.extensions.call
import com.wire.extensions.createLogger
import com.wire.extensions.instance
import com.wire.extensions.respondWithStatus
import com.wire.service.UserService
import com.wire.service.access.AccessService
import io.ktor.http.HttpStatusCode
import java.time.Instant
import java.util.UUID

private val logger = createLogger("LoginRoute")


fun NormalOpenAPIRoute.loginRoutes() {
    val accessService by instance<AccessService>()
    val userService by instance<UserService>()

    val mapper by instance<ObjectMapper>()

    route("/api-version").get<Unit, VersionInfo> {
        respond(
            VersionInfo(
                federation = false,
                supported = listOf(
//                    0,
                    1,
//                    2
                ),
                domain = "localhost.local",
                developmentSupported = null
            )
        )
    }

    route("${VERSION}/login").post<Unit, AccessTokenDTO, LoginRequest>(
        info(summary = "Logins user"),
    ) { _, login ->

        val (accessToken, refreshToken) = accessService
            .generateTokens(email = login.email, password = Password(login.password))

        call.response.cookies.append("zuid", refreshToken())
        respond(accessToken)
    }

    route("${VERSION}/access").post<Unit, AccessTokenDTO, Unit>(
        info(summary = "Issues access token."),
    ) { _, _ ->
        val zuidCookie = call.request.cookies["zuid"]!!
        val (accessToken, refreshToken) = accessService
            .generateTokens(refreshToken = RefreshToken(zuidCookie))

        call.response.cookies.append("zuid", refreshToken())
        respond(accessToken)
    }

    route("${VERSION}/self").get<Unit, UserDTO> {
        // todo get this from the auth context
        val selfId = QualifiedId("", UUID.randomUUID())

        val user = userService.getUserById(selfId)
            ?: throw EntityNotFoundException<UserDTO>("id = $selfId") // todo: this should not happen

        respond(user)
    }

    route("${VERSION}/clients").post<Unit, ClientResponse, RegisterClientRequest>(
        info(summary = "Registers client"),
    ) { _, registration ->
        logger.info { registration.type }

        respond(
            ClientResponse(
                cookie = "cookie",
                registrationTime = mapper.writeValueAsString(Instant.now()),
                model = registration.model,
                clientId = "client_1",
                type = "permanent",
                capabilities = Capabilities(),
                label = registration.label,
                mlsPublicKeys = null,
                location = null
            )
        )
    }

    route("${VERSION}/mls/key-packages/self/{clientId}").post<ClientIdParam, OkResponse, KeyPackageList> { clientId, list ->
        logger.info { clientId }
        respondWithStatus(HttpStatusCode.Created, OkResponse("created"))
    }

    route("${VERSION}/mls/key-packages/self/{clientId}/count").get<ClientIdParam, KeyPackageCountDTO> { clientId ->
        logger.info { clientId }
        respond(
            KeyPackageCountDTO(
                10
            )
        )
    }
}

data class OkResponse(val status: String = "ok")

data class KeyPackageList(
    @JsonProperty("key_packages") val keyPackages: List<KeyPackage>
)

internal data class ClientIdParam(@PathParam("Client ID") val clientId: String)

internal data class KeyPackageCountDTO(
    @JsonProperty("count") val count: Int
)


internal data class ClientResponse(
    @JsonProperty("cookie") val cookie: String?,
    @JsonProperty("time") val registrationTime: String, // yyyy-mm-ddThh:MM:ss.qqq
    @JsonProperty("location") val location: LocationResponse?,
    @JsonProperty("model") val model: String? = null,
    @JsonProperty("id") val clientId: String,
    @JsonProperty("type") val type: String = "permanent", // temporary, pernament, legalhold
    @JsonProperty("class") val deviceType: String = "unknown",
    @JsonProperty("capabilities") val capabilities: Capabilities?,
    @JsonProperty("label") val label: String? = null,
    @JsonProperty("mls_public_keys") val mlsPublicKeys: Map<String, String>?
)

data class LocationResponse(
    @JsonProperty("lat") val latitude: String,
    @JsonProperty("lon") val longitude: String
)

data class Capabilities(
    @JsonProperty("capabilities") val capabilities: List<String> = listOf("legalhold-implicit-consent")
)

internal data class RegisterClientRequest(
    @JsonProperty("password") val password: String?,
    @JsonProperty("prekeys") val preKeys: List<Any>, // we don't care
    @JsonProperty("lastkey") val lastKey: Any, // we don't care
    @JsonProperty("class") val deviceType: String?,
    @JsonProperty("type") val type: String, // 'temporary', 'permanent', 'legalhold'
    @JsonProperty("label") val label: String?,
    @JsonProperty("capabilities") val capabilities: List<Any>?, // we don't care
    @JsonProperty("model") val model: String?
)


internal data class VersionInfo(
    @JsonProperty("federation") val federation: Boolean,
    @JsonProperty("supported") val supported: List<Int>,
    @JsonProperty("domain") val domain: String? = null,
    @JsonProperty("development") val developmentSupported: List<Int>? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class LoginRequest(
    val email: String,
    val password: String,
    val label: String
) {
    override fun toString(): String {
        return "LoginRequest(email='$email', label='$label')"
    }
}

internal data class LoginResponse(
    @JsonProperty("user") val userId: NonQualifiedUserId,
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("token_type") val tokenType: String
)

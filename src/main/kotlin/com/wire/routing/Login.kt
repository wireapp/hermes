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
import com.wire.dto.KeyPackage
import com.wire.dto.NonQualifiedUserId
import com.wire.dto.QualifiedId
import com.wire.dto.TeamId
import com.wire.dto.UserId
import com.wire.extensions.call
import com.wire.extensions.instance
import com.wire.extensions.createLogger
import com.wire.extensions.respondWithStatus
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import org.kodein.di.instance
import java.time.Instant
import java.util.UUID

private val logger = createLogger("LoginRoute")


fun NormalOpenAPIRoute.loginRoutes() {

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

    route("${VERSION}/login").post<Unit, LoginResponse, LoginRequest>(
        info(summary = "Logins user"),
    ) { _, login ->
        logger.info { login.toString() }

        call.response.cookies.append("zuid", login.email)
        respond(
            LoginResponse(
                userId = login.email,
                accessToken = login.email,
                expiresIn = 100000,
                tokenType = "Bearer"
            )
        )
    }

    route("${VERSION}/access").post<Unit, LoginResponse, Unit>(
        info(summary = "Issues access token."),
    ) { _, _ ->
        val email = call.request.cookies["zuid"]!!

        call.response.cookies.append("zuid", email)
        respond(
            LoginResponse(
                userId = email,
                accessToken = email,
                expiresIn = 100000,
                tokenType = "Bearer"
            )
        )
    }

    route("${VERSION}/self").get<Unit, UserDTO> {
        val email = call.request.header("Authorization")!!.substringAfter("Bearer ")
        respond(
            UserDTO(
                accentId = 0,
                assets = emptyList(),
                deleted = false,
                email = email,
                expiresAt = null,
                handle = email,
                nonQualifiedId = email,
                name = email,
                locale = "en-us",
                id = QualifiedId(email, "localhost.local"),
                teamId = null
            )
        )
    }

    route("${VERSION}/clients").post<Unit, ClientResponse, RegisterClientRequest>(
        info(summary = "Logins user"),
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


internal data class UserAssetDTO(
    @JsonProperty("key") val key: String,
    @JsonProperty("size") val size: String?,
    @JsonProperty("type") val type: String
)

internal data class UserDTO(
    @JsonProperty("accent_id") val accentId: Int,
    @JsonProperty("assets") val assets: List<UserAssetDTO>,
    @JsonProperty("deleted") val deleted: Boolean?,
    @JsonProperty("email") val email: String?,
    @JsonProperty("expires_at") val expiresAt: String?,
    @JsonProperty("handle") val handle: String?,
    @Deprecated("use id instead", replaceWith = ReplaceWith("this.id"))
    @JsonProperty("id") val nonQualifiedId: NonQualifiedUserId,
    @JsonProperty("name") val name: String,
    @JsonProperty("locale") val locale: String,
    @JsonProperty("managed_by") val managedByDTO: Any? = null,
    @JsonProperty("phone") val phone: String? = null,
    @JsonProperty("qualified_id") val id: UserId,
    @JsonProperty("service") val service: Any? = null,
    @JsonProperty("sso_id") val ssoID: Any? = null,
    @JsonProperty("team") val teamId: TeamId?
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
)

internal data class LoginResponse(
    @JsonProperty("user") val userId: NonQualifiedUserId,
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("token_type") val tokenType: String
)

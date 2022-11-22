package com.wire.routing

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.wire.dto.ConversationId
import com.wire.dto.NonQualifiedUserId
import com.wire.dto.QualifiedId
import com.wire.dto.TeamId
import com.wire.dto.UserId
import com.wire.extensions.call
import com.wire.extensions.createLogger

private val logger = createLogger("UsersRoutes")


fun NormalOpenAPIRoute.usersRoutes() {
    // TODO: this is going to be a problem because of the generic list
    route("${VERSION}/list-users").post<Unit, List<UserProfileDTO>, Unit>(
        info(summary = "Lists users"),
    ) { _, _ ->
        respond(
            emptyList()
        )
    }

    route("${VERSION}/users/{domain}/{handle}").get<UserPathParam, UserProfileDTO>(
        info(summary = "Get specific user"),
    ) { (domain, handle) ->
        respond(
            UserProfileDTO(
                id = QualifiedId(handle, domain),
                name = handle,
                handle = handle,
                legalHoldStatus = LegalHoldStatusResponse.DISABLED,
                teamId = null,
                accentId = 0,
                assets = listOf(),
                deleted = false,
                email = handle,
                expiresAt = null,
                nonQualifiedId = handle,
                service = null
            )
        )
    }

}

internal data class UserPathParam(@PathParam("domain") val domain: String, @PathParam("handle") val handle: String)

internal data class UserProfileDTO(
    @JsonProperty("qualified_id") val id: UserId,
    @JsonProperty("name") val name: String,
    @JsonProperty("handle") val handle: String?,
    @JsonProperty("legalhold_status") val legalHoldStatus: LegalHoldStatusResponse,
    @JsonProperty("team") val teamId: TeamId?,
    @JsonProperty("accent_id") val accentId: Int,
    @JsonProperty("assets") val assets: List<UserAssetDTO>,
    @JsonProperty("deleted") val deleted: Boolean?,
    @JsonProperty("email") val email: String?,
    @JsonProperty("expires_at") val expiresAt: String?,
    @Deprecated("use id instead", replaceWith = ReplaceWith("this.id"))
    @JsonProperty("id") val nonQualifiedId: NonQualifiedUserId,
    @JsonProperty("service") val service: ServiceDTO?
)

internal data class ServiceDTO(
    @JsonProperty("id") val id: String,
    @JsonProperty("provider") val provider: String
)

enum class LegalHoldStatusResponse {
    @JsonProperty("enabled")
    ENABLED,

    @JsonProperty("pending")
    PENDING,

    @JsonProperty("disabled")
    DISABLED,

    @JsonProperty("no_consent")
    NO_CONSENT
}

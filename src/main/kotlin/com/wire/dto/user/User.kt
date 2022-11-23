package com.wire.dto.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.wire.dao.NonQualifiedUserId
import com.wire.dao.TeamId
import com.wire.dao.UserId

data class UserDTO(
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
    @JsonProperty("managed_by") val managedBy: Any? = null,
    @JsonProperty("phone") val phone: String? = null,
    @JsonProperty("qualified_id") val id: UserId,
    @JsonProperty("service") val service: Any? = null,
    @JsonProperty("sso_id") val ssoId: Any? = null,
    @JsonProperty("team") val teamId: TeamId?
)


data class UserProfileDTO(
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

data class ServiceDTO(
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

data class UserAssetDTO(
    @JsonProperty("key") val key: String,
    @JsonProperty("size") val size: String?,
    @JsonProperty("type") val type: String
)

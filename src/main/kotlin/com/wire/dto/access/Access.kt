package com.wire.dto.access

import com.fasterxml.jackson.annotation.JsonProperty
import com.wire.dao.NonQualifiedUserId
import com.wire.dto.user.SensitiveString

data class AccessTokenDTO(
    @JsonProperty("user") val userId: NonQualifiedUserId,
    @JsonProperty("access_token") val token: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("token_type") val tokenType: String
)

typealias RefreshToken = SensitiveString

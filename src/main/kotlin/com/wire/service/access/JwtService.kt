package com.wire.service.access

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.wire.dao.QualifiedId
import com.wire.dao.UserId
import com.wire.dto.access.AccessTokenDTO
import com.wire.dto.user.SensitiveString
import com.wire.setup.configuration.auth.JwtConfiguration
import dev.forst.katlib.InstantProvider
import dev.forst.katlib.toUuid
import io.ktor.server.auth.jwt.JWTCredential
import java.util.Date

/**
 * Service for issuing JWTs.
 */
class JwtService(
    private val algorithm: Algorithm,
    private val jwtConfiguration: JwtConfiguration,
    private val nowProvider: InstantProvider,
    private val verifier: JWTVerifier
) {

    /**
     * Generate and sign JWT for given principal.
     */
    fun generateToken(userId: UserId): AccessTokenDTO {
        val now = nowProvider.now()
        val expires = now + jwtConfiguration.expiration
        // TODO put most to the claims
        val token = JWT.create()
            .withIssuer(jwtConfiguration.issuer)
            .withIssuedAt(Date(now.toEpochMilli()))
            .withExpiresAt(Date(expires.toEpochMilli()))
            .withSubject(userId.toString())
            .withAudience(userId.domain)
            .sign(algorithm)

        return AccessTokenDTO(
            userId = userId.id,
            token = token,
            expiresIn = jwtConfiguration.expiration.seconds.toInt(),
            tokenType = "Bearer"
        )
    }

    /**
     * Generates refresh token for the given user.
     */
    fun generateRefreshToken(userId: UserId): SensitiveString {
        // TODO use server persisted server, right now we just need some string
        val now = nowProvider.now()
        val expires = (now + jwtConfiguration.expiration).toEpochMilli() * 10
        val token = JWT.create()
            .withIssuer(jwtConfiguration.issuer)
            .withIssuedAt(Date(now.toEpochMilli()))
            .withExpiresAt(Date(expires))
            .withSubject(userId.toString())
            .withAudience(userId.domain)
            .sign(algorithm)
        return SensitiveString(token)
    }

    // TODO will be different
    fun userIdFromRefreshToken(token: String?): UserId = userIdFromAccessToken(token)
    fun userIdFromRefreshToken(token: JWTCredential): UserId = userIdFromAccessToken(token)

    /**
     * Parse and verify that [token] is JWT signed by this application.
     */
    fun userIdFromAccessToken(token: String?): UserId = runCatching {
        if (token != null) {
            val verifiedToken = verifier.verify(token)
            QualifiedId(
                domain = verifiedToken.audience.single(),
                id = verifiedToken.subject.toUuid()
            )
        } else null
    }.getOrNull() ?: throw Exception("Invalid JWT!")

    /**
     * Create principal from the JWT.
     */
    fun userIdFromAccessToken(credential: JWTCredential?): UserId =
        runCatching {
            if (credential != null) {
                QualifiedId(
                    domain = credential.audience.single(),
                    id = credential.subject!!.toUuid()
                )
            } else null
        }.getOrNull() ?: throw Exception("Invalid JWT!") // TODO: fix exceptions here
}

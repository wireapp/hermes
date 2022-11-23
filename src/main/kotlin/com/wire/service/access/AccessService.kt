package com.wire.service.access

import com.wire.dao.QualifiedId
import com.wire.dto.access.AccessTokenDTO
import com.wire.dto.access.RefreshToken
import com.wire.dto.user.Password
import com.wire.error.AuthenticationFailedException
import com.wire.service.UserService

class AccessService(
    private val usersService: UserService,
    private val passwordHashingService: PasswordHashingService,
    private val jwtService: JwtService
) {
    /**
     * Generates access token for given [email] if the password matches.
     */
    suspend fun generateTokens(email: String, password: Password) =
        assertCorrectCredentials(email, password)
            .let(::tokensForId)

    /**
     * Generates access and refresh token for given [refreshToken].
     */
    suspend fun generateTokens(refreshToken: RefreshToken) =
        jwtService
            .userIdFromRefreshToken(refreshToken())
            .let(::tokensForId)

    /**
     * Returns [QualifiedId] of the user if the passwords match.
     */
    suspend fun assertCorrectCredentials(email: String, password: Password): QualifiedId {
        val (id, passwordHash) = usersService.getUsersPasswordHash(email)
            ?: throw AuthenticationFailedException()

        // now verify password
        val correctPassword = passwordHashingService
            .isPasswordCorrect(password, passwordHash)
        // and return ID if match
        if (correctPassword) {
            return id
        } else {
            throw AuthenticationFailedException()
        }
    }

    private fun tokensForId(id: QualifiedId): Pair<AccessTokenDTO, RefreshToken> = Pair(
        jwtService.generateToken(id),
        jwtService.generateRefreshToken(id)
    )
}

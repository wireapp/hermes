package com.wire.service.access

import com.lambdaworks.crypto.SCryptUtil
import com.wire.dto.user.Password
import mu.KLogging

/**
 * Provider which hashes passwords.
 */
class PasswordHashingService {

    private companion object : KLogging() {
        // see https://github.com/wg/scrypt
        const val N = 16384
        const val r = 8
        const val p = 1
    }

    /**
     * Creates hash from given [password] using SCrypt algorithm.
     */
    fun hashPassword(password: Password): String =
        SCryptUtil.scrypt(password(), N, r, p)

    /**
     * Verifies that the [password] matches given [passwordHash].
     * Returns true if they match, returns false otherwise.
     */
    fun isPasswordCorrect(password: Password, passwordHash: String): Boolean =
        SCryptUtil.check(password(), passwordHash)
}


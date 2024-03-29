package com.wire.setup.configuration.auth

import com.sksamuel.hoplite.Masked
import java.time.Duration

data class JwtConfiguration(
    val secret: Masked,
    val issuer: String,
    val expiration: Duration
)

@file:Suppress("RemoveExplicitTypeArguments") // this is ok, we want to be explicit for better readability

package com.wire.setup.di

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.wire.dao.Database
import com.wire.extensions.createLogger
import com.wire.service.UserService
import com.wire.service.access.AccessService
import com.wire.service.access.JwtService
import com.wire.service.access.PasswordHashingService
import com.wire.setup.configuration.DatabaseConfiguration
import com.wire.setup.configuration.auth.JwtConfiguration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.forst.katlib.InstantProvider
import dev.forst.katlib.jacksonMapper
import org.flywaydb.core.Flyway
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.security.SecureRandom

private val logger = createLogger("DependencyInjection")

/**
 * Register instances that are created only when needed.
 */
@Suppress("LongMethod") // this is ok, configuration method
fun DI.MainBuilder.registerClasses() {
    logger.info { "Registering DI container." }

    registerDatabase()

    bindSingleton {
        jacksonMapper().apply {
            registerModule(JavaTimeModule())
            // use ie. 2021-03-15T13:55:39.813985Z instead of 1615842349.47899
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
    bindSingleton { InstantProvider }
    bindSingleton { SecureRandom() }


    bindSingleton { UserService(instance(), instance()) }

    // auth
    // jwt security
    bindSingleton {
        Algorithm.HMAC256(instance<JwtConfiguration>().secret.value)
    }
    bindSingleton {
        JWT.require(instance())
            .withIssuer(instance<JwtConfiguration>().issuer)
            .build()
    }
    bindSingleton { JwtService(instance(), instance(), instance(), instance()) }
    bindSingleton { AccessService(instance(), instance(), instance()) }
    bindSingleton { PasswordHashingService() }
}

private fun DI.MainBuilder.registerDatabase() {
    bindSingleton { HikariDataSource(instance()) }

    bindSingleton {
        val dbConfiguration = instance<DatabaseConfiguration>()

        HikariConfig().apply {
            jdbcUrl = "${dbConfiguration.url}?reWriteBatchedInserts=true"
            username = dbConfiguration.userName
            password = dbConfiguration.password.value
            isAutoCommit = false // see https://github.com/raharrison/kotlin-ktor-exposed-starter/issues/106
        }
    }

    bindSingleton {
        Flyway
            .configure()
            .dataSource(instance())
            .load()
    }

    bindSingleton { Database.connect(instance()) }
}

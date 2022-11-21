package com.wire.base

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.preprocessor.EnvOrSystemPropertyPreprocessor
import com.sksamuel.hoplite.preprocessor.LookupPreprocessor
import com.wire.setup.configuration.DatabaseConfiguration
import com.wire.setup.di.registerClasses
import com.wire.setup.di.registerConfiguration
import com.wire.utils.mockedNowProvider
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindConstant
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.util.UUID

/**
 * Test base that has access to initialized dependency injection.
 *
 * Use [overrideDIContainer] to inject and override additional dependencies.
 */
open class DiAwareTestBase : DIAware {

    companion object {
        protected val configuration = ConfigLoader.builder()
            .addPreprocessor(EnvOrSystemPropertyPreprocessor)
            .addPreprocessor(LookupPreprocessor)
            .allowUnresolvedSubstitutions()
            .addSource(PropertySource.resource("/application.yml"))
            .build()
            .loadConfigOrThrow<com.wire.setup.configuration.Configuration>()
    }

    protected val rootDI = DI(allowSilentOverride = true) {
        registerConfiguration(configuration)

        // register rest of the instances
        registerClasses()

        bindConstant("db-name") {
            "test_${UUID.randomUUID().toString().replace("-", "")}"
        }

        bindSingleton("root-ds") {
            val dbConfiguration = instance<DatabaseConfiguration>()
            HikariDataSource(
                HikariConfig().apply {
                    jdbcUrl = dbConfiguration.url
                    username = dbConfiguration.userName
                    password = dbConfiguration.password.value
                    maximumPoolSize = 1
                }
            )
        }

        bindSingleton {
            val dbConfiguration = instance<DatabaseConfiguration>()

            val testDbName = instance<String>("db-name")
            HikariConfig().apply {
                // regarding prepareThreshold see https://stackoverflow.com/q/2783813/7169288
                jdbcUrl = "${
                    dbConfiguration.url.substringBeforeLast('/')
                }/$testDbName?reWriteBatchedInserts=true&prepareThreshold=0"
                username = dbConfiguration.userName
                password = dbConfiguration.password.value
                // limit pool to just two threads, so we won't overheat Postgres during parallel tests
                maximumPoolSize = 2
            }
        }

        bindSingleton { mockedNowProvider() }

        overrideDIContainer()?.let { import(it, allowOverride = true) }
    }

    /**
     * Override this if you want to add additional bindings or if you want to override
     * some instances from the base DI container.
     */
    protected open fun overrideDIContainer(): DI.Module? = null

    protected fun module(init: DI.Builder.() -> Unit) =
        DI.Module("TestOverridingModule", true, init = init)

    override val di: DI
        get() = rootDI
}



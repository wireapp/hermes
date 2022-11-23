@file:Suppress("DuplicatedCode") // not duplicate, there are generics involved

package com.wire.setup.di

import com.wire.setup.configuration.Configuration
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

/**
 * Registers given [configuration] in the DI, should be called before [registerClasses] and rest of the DI.
 */
fun DI.MainBuilder.registerConfiguration(configuration: Configuration) {
    // register configuration
    bindSingleton { configuration }
    // for convenience register all parts of the configuration as well
    bindSingleton { instance<Configuration>().application }
    bindSingleton { instance<Configuration>().database }
    bindSingleton { instance<Configuration>().swagger }
    bindSingleton { instance<Configuration>().server }
    bindSingleton { instance<Configuration>().federation }
    bindSingleton { instance<Configuration>().jwt }
}

package com.wire.extensions

import mu.KLogging

/**
 * Creates logger with given name.
 */
fun createLogger(name: String) = KLogging().logger("com.wire.hermes.$name")

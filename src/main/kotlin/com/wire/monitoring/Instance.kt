package com.wire.monitoring

import java.util.UUID

/**
 * Static identification of the running instance.
 */
val INSTANCE: String by lazy { UUID.randomUUID().toString() }

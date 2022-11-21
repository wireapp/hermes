package com.wire.monitoring

/**
 * MDC variable name for the request id.
 */
const val REQUEST_ID = "requestId"

/**
 * Remote host - IP address of the caller.
 */
const val REMOTE_HOST = "remoteHost"

/**
 * Ktor path in the request.
 */
const val PATH = "path"

/**
 * Generated stable ID of the instance - it should be generated
 * during the application startup and don't change during the
 * whole application lifecycle.
 */
const val INSTANCE_ID = "instance"

/**
 * ID of the current session if set.
 */
const val SESSION_ID = "sessionId"

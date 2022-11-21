package com.wire.error

import com.wire.monitoring.INSTANCE

/**
 * Generic response that is returned to the user if the backend
 * throws an exception.
 */
data class ErrorResponse(
    /**
     * HTTP code.
     */
    val code: Int,
    /**
     * Information about what happened.
     */
    val message: String,
    /**
     * Type of exception that occurred. This is used by FE to set proper error text.
     */
    val label: ErrorType? = null,
    /**
     * ID of the request.
     */
    val requestId: String? = null,
    /**
     * ID of the instance that handled request.
     */
    val instanceId: String = INSTANCE,
    /**
     * Any other details about the error.
     */
    val detail: Any? = null
)

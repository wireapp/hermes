package com.wire.routing

/**
 * All routes in the application.
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "MemberNameEqualsClassName",
    "SameParameterValue",
) // can be used in tests
object Routes {
    /**
     * Prefix for all public REST APIs.
     */
    const val apiPrefix = "/api"

    val version = apiName("version")
    val status = apiName("status")
    val statusHealth = "$status/health"

    /**
     * URL for openapi json.
     */
    const val openApiJson = "/openapi.json"

    /**
     * URL for swagger UI.
     */
    const val swaggerUi = "/swagger-ui"

    /**
     * Public APIs.
     */
    private fun apiName(name: String) = "$apiPrefix/$name"
}

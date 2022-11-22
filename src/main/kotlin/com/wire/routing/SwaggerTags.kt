package com.wire.routing

import com.papsign.ktor.openapigen.APITag

/**
 * Tags for the endpoints that display different routes categories.
 */
@Suppress("EnumEntryName", "EnumNaming") // because we want to have reasonable names in swagger
enum class SwaggerTags(override val description: String) : APITag {
    service("Service endpoints without business logic."),
    auth("Auth related endpoints.")
}

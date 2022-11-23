package com.wire.routing

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.wire.dao.QualifiedHandle
import com.wire.dao.QualifiedId
import com.wire.dto.user.UserProfileDTO
import com.wire.error.EntityNotFoundException
import com.wire.extensions.createLogger
import com.wire.extensions.instance
import com.wire.service.UserService

private val logger = createLogger("UsersRoutes")


fun NormalOpenAPIRoute.usersRoutes() {
    val userService by instance<UserService>()

    // TODO: this is going to be a problem because of the generic list which Java erases
    route("${VERSION}/list-users").post<Unit, List<UserProfileDTO>, ListUserRequest>(
        info(summary = "Lists users"),
    ) { _, request ->
        val idUsers =
            if (request.qualifiedIds.isNotEmpty()) {
                userService.getUserProfilesByIds(request.qualifiedIds)
            } else emptyList()

        val handleUsers =
            if (request.qualifiedHandles.isNotEmpty()) {
                userService.getUserProfilesByHandles(request.qualifiedHandles)
            } else emptyList()

        respond(idUsers + handleUsers)
    }

    route("${VERSION}/users/{domain}/{handle}").get<UserPathParam, UserProfileDTO>(
        info(summary = "Get specific user"),
    ) { (domain, handle) ->
        val maybeUser = userService.getUserProfileByHandle(QualifiedHandle(domain, handle))
            ?: throw EntityNotFoundException<UserProfileDTO>("handle = $handle")

        respond(maybeUser)
    }
}

internal data class UserPathParam(
    @PathParam("domain") val domain: String, @PathParam("handle") val handle: String
)

internal data class ListUserRequest(
    @JsonProperty("qualified_ids") val qualifiedIds: List<QualifiedId> = emptyList(),
    @JsonProperty("qualified_handles") val qualifiedHandles: List<QualifiedHandle> = emptyList()
)

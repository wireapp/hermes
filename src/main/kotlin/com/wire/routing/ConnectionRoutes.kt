package com.wire.routing

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.wire.dto.ConversationId
import com.wire.dto.UserId
import com.wire.extensions.call
import com.wire.extensions.createLogger

private val logger = createLogger("ConnectionRoutes")


fun NormalOpenAPIRoute.connectionRoutes() {

    route("${VERSION}/list-connections").post<Unit, ConnectionResponse, PaginationRequest>(
        info(summary = "Logins user"),
    ) { _, paging ->
        logger.info { paging.toString() }

        respond(
            ConnectionResponse(
                connections = listOf(),
                hasMore = false,
                pagingState = paging.pagingState ?: ""
            )
        )
    }

}

data class ConnectionResponse(
    @JsonProperty("connections") val connections: List<ConnectionDTO>,
    @JsonProperty("has_more") val hasMore: Boolean,
    @JsonProperty("paging_state") val pagingState: String
)

data class ConnectionDTO(
    @JsonProperty("conversation") val conversationId: String,
    @JsonProperty("from") val from: String,
    @JsonProperty("last_update") val lastUpdate: String,
    @JsonProperty("qualified_conversation") val qualifiedConversationId: ConversationId,
    @JsonProperty("qualified_to") val qualifiedToId: UserId,
    @JsonProperty("status") val status: ConnectionStateDTO,
    @JsonProperty("to") val toId: String
)

enum class ConnectionStateDTO {
    /** The other user has sent a connection request to this one */
    @JsonProperty("pending")
    PENDING,

    /** This user has sent a connection request to another user */
    @JsonProperty("sent")
    SENT,

    /** The user has been blocked */
    @JsonProperty("blocked")
    BLOCKED,

    /** The connection has been ignored */
    @JsonProperty("ignored")
    IGNORED,

    /** The connection has been cancelled */
    @JsonProperty("cancelled")
    CANCELLED,

    /** The connection is missing legal hold consent  */
    @JsonProperty("missing-legalhold-consent")
    MISSING_LEGALHOLD_CONSENT,

    /** The connection is complete and the conversation is in its normal state */
    @JsonProperty("accepted")
    ACCEPTED
}

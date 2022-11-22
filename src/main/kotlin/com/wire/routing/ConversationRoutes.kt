package com.wire.routing

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.wire.dto.ConversationId
import com.wire.dto.TeamId
import com.wire.dto.UserId
import com.wire.extensions.call
import com.wire.extensions.createLogger

private val logger = createLogger("ConversationRoutes")


fun NormalOpenAPIRoute.conversationRoutes() {

    route("${VERSION}/conversations/list-ids").post<Unit, ConversationPagingResponse, PaginationRequest>(
        info(summary = "Lists Conversations"),
    ) { _, paging ->
        logger.info { paging.toString() }

        respond(
            ConversationPagingResponse(
                conversationsIds = listOf(),
                hasMore = false,
                pagingState = paging.pagingState ?: ""
            )
        )
    }

    route("${VERSION}/conversations/list/v2").post<Unit, ConversationResponseDTO, ConversationsDetailsRequest>(
        info(summary = "Lists Conversations"),
    ) { _, request ->
        logger.info { request.toString() }

        respond(
            ConversationResponseDTO(
                conversationsFound = listOf(),
                conversationsNotFound = listOf(),
                conversationsFailed = listOf()
            )
        )
    }


}

data class ConversationsDetailsRequest(
    @JsonProperty("qualified_ids")
    val conversationsIds: List<ConversationId>,
)


data class PaginationRequest(
    @JsonProperty("paging_state") val pagingState: String?,
    @JsonProperty("size") val size: Int? = null // Set in case you want specific number of pages, otherwise, the backend will return default per endpoint
)


data class ConversationPagingResponse(
    @JsonProperty("qualified_conversations") val conversationsIds: List<ConversationId>,
    @JsonProperty("has_more") val hasMore: Boolean,
    @JsonProperty("paging_state") val pagingState: String?
)

data class ConversationResponseDTO(
    @JsonProperty("found") val conversationsFound: List<ConversationResponse>,
    @JsonProperty("not_found") val conversationsNotFound: List<ConversationId>,
    @JsonProperty("failed") val conversationsFailed: List<ConversationId>,
)


data class ConversationResponse(
    @JsonProperty("creator")
    val creator: String,

    @JsonProperty("members")
    val members: ConversationMembersResponse,

    @JsonProperty("name")
    val name: String?,

    @JsonProperty("qualified_id")
    val id: ConversationId,

    @JsonProperty("group_id")
    val groupId: String?,

    @JsonProperty("epoch")
    val epoch: ULong?,

    @JsonProperty("epoch")
    val type: Int, // Type

    @JsonProperty("message_timer")
    val messageTimer: Long?,

    @JsonProperty("team")
    val teamId: TeamId?,

    @JsonProperty("protocol")
    val protocol: String, // mls, proteus

    @JsonProperty("last_event_time")
    val lastEventTime: String,

    @JsonProperty("cipher_suite")
    val mlsCipherSuiteTag: Int?,

    @JsonProperty("access") val access: Set<ConversationAccessDTO>,
    @JsonProperty("access_role_v2") val accessRole: Set<ConversationAccessRoleDTO> = ConversationAccessRoleDTO.DEFAULT_VALUE_WHEN_NULL,
) {

    enum class Type(val id: Int) {
        GROUP(0), SELF(1), ONE_TO_ONE(2), WAIT_FOR_CONNECTION(3), INCOMING_CONNECTION(4);

        companion object {
            fun fromId(id: Int): Type = values().first { type -> type.id == id }
        }
    }
}

data class ConversationMembersResponse(
    @JsonProperty("self")
    val self: ConversationMemberDTO.Self,

    @JsonProperty("others")
    val otherMembers: List<ConversationMemberDTO.Other>
)

sealed class ConversationMemberDTO {
    // Role name, between 2 and 128 chars, 'wire_' prefix is reserved for roles designed
    // by Wire (i.e., no custom roles can have the same prefix)
    // in swagger conversation_role is an optional field but according to Akshay:
    // Hmm, the field is optional when sending it to the server. The server will always send the field.
    // (The server assumes admin when the field is missing, I don't have the context behind this decision)
    abstract val conversationRole: String
    abstract val id: UserId
    abstract val service: ServiceReferenceDTO?


    data class Self(
        @JsonProperty(ID_SERIAL_NAME) override val id: UserId,
        @JsonProperty(CONV_ROLE_SERIAL_NAME) override val conversationRole: String,
        @JsonProperty(SERVICE_SERIAL_NAME) override val service: ServiceReferenceDTO? = null,
        @JsonProperty("hidden") val hidden: Boolean? = null,
        @JsonProperty("hidden_ref") val hiddenRef: String? = null,
        @JsonProperty("otr_archived") val otrArchived: Boolean? = null,
        @JsonProperty("otr_archived_ref") val otrArchivedRef: String? = null,
        @JsonProperty("otr_muted_ref") val otrMutedRef: String? = null,
        @JsonProperty("otr_muted_status") val otrMutedStatus: Int? = null // MutedStatus
    ) : ConversationMemberDTO()


    data class Other(
        @JsonProperty(ID_SERIAL_NAME) override val id: UserId,
        @JsonProperty(CONV_ROLE_SERIAL_NAME) override val conversationRole: String,
        @JsonProperty(SERVICE_SERIAL_NAME) override val service: ServiceReferenceDTO? = null
    ) : ConversationMemberDTO()

    private companion object {
        const val ID_SERIAL_NAME = "qualified_id"
        const val CONV_ROLE_SERIAL_NAME = "conversation_role"
        const val SERVICE_SERIAL_NAME = "service"
    }
}


data class ServiceReferenceDTO(
    @JsonProperty("id")
    val id: String,

    @JsonProperty("provider")
    val provider: String
)


enum class MutedStatus {
    /**
     * 0 -> All notifications are displayed
     */
    ALL_ALLOWED,

    /**
     * 1 -> Only mentions are displayed (normal messages muted)
     */
    ONLY_MENTIONS_ALLOWED,

    /**
     * 2 -> Only normal notifications are displayed (mentions are muted) -- legacy, not used
     */
    MENTIONS_MUTED,

    /**
     * 3 -> No notifications are displayed
     */
    ALL_MUTED;

    companion object {
        fun fromOrdinal(ordinal: Int): MutedStatus? = values().firstOrNull { ordinal == it.ordinal }
    }
}



enum class ConversationAccessDTO {
    @JsonProperty("private")
    PRIVATE,
    @JsonProperty("code")
    CODE,
    @JsonProperty("invite")
    INVITE,
    @JsonProperty("link")
    LINK;

    override fun toString(): String {
        return this.name.lowercase()
    }
}

enum class ConversationAccessRoleDTO {
    @JsonProperty("team_member")
    TEAM_MEMBER,
    @JsonProperty("non_team_member")
    NON_TEAM_MEMBER,
    @JsonProperty("guest")
    GUEST,
    @JsonProperty("service")
    SERVICE,
    @JsonProperty("partner")
    EXTERNAL;

    override fun toString(): String {
        return this.name.lowercase()
    }

    companion object {
        val DEFAULT_VALUE_WHEN_NULL = setOf(TEAM_MEMBER, NON_TEAM_MEMBER, SERVICE)
    }
}


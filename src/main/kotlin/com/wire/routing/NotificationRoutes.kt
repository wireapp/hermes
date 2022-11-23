package com.wire.routing

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.wire.extensions.createLogger
import java.util.UUID

private val logger = createLogger("NotificationRoutes")


fun NormalOpenAPIRoute.notificationRoutes() {
    route("${VERSION}/notifications/last").get<ClientIdQueryParam, EventResponse>(
        info(summary = "Gets last notification"),
    ) { client ->
        respond(
            EventResponse(
                id = UUID.randomUUID().toString(),
                payload = listOf()
            )
        )
    }
}

internal data class ClientIdQueryParam(@QueryParam("client") val client: String)

data class EventResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("payload") val payload: List<EventContentDTO>?,
    @JsonProperty("transient") val transient: Boolean = false
)


typealias EventContentDTO = Any
//sealed class EventContentDTO {
//
//
//    sealed class Conversation : EventContentDTO() {
//
//
//        @JsonProperty("conversation.create")
//        data class NewConversationDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String,
//            @JsonProperty("data") val data: ConversationResponse,
//        ) : Conversation()
//
//
//        @JsonProperty("conversation.delete")
//        data class DeletedConversationDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String
//        ) : Conversation()
//
//
//        @JsonProperty("conversation.rename")
//        data class ConversationRenameDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String,
//            @JsonProperty("data") val updateNameData: ConversationNameUpdateEvent,
//        ) : Conversation()
//
//
//        @JsonProperty("conversation.member-join")
//        data class MemberJoinDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String,
//            @JsonProperty("data") val members: ConversationMembers,
//            @Deprecated("use qualifiedFrom", replaceWith = ReplaceWith("this.qualifiedFrom")) @JsonProperty("from") val from: String
//        ) : Conversation()
//
//
//        @JsonProperty("conversation.member-leave")
//        data class MemberLeaveDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String,
//            // TODO: rename members to something else since the name is confusing (it's only userIDs)
//            @JsonProperty("data") val members: ConversationUsers,
//            @JsonProperty("from") val from: String
//        ) : Conversation()
//
//
//        @JsonProperty("conversation.member-update")
//        data class MemberUpdateDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String,
//            @JsonProperty("from") val from: String,
//            @JsonProperty("data") val roleChange: ConversationRoleChange
//        ) : Conversation()
//
//        // TODO conversation.typing
//
//
//        @JsonProperty("conversation.otr-message-add")
//        data class NewMessageDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String,
//            @JsonProperty("data") val data: MessageEventData,
//        ) : Conversation()
//
//
//        @JsonProperty("conversation.access-update")
//        data class AccessUpdate(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("data") val data: ConversationAccessInfoDTO,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//        ) : Conversation()
//
//        // TODO conversation.code-update
//
//        // TODO conversation.code-delete
//
//        // TODO conversation.receipt-mode-update
//
//        // TODO conversation.message-timer-update
//
//
//        @JsonProperty("conversation.mls-message-add")
//        data class NewMLSMessageDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            val time: String,
//            @JsonProperty("data") val message: String,
//        ) : Conversation()
//
//
//        @JsonProperty("conversation.mls-welcome")
//        data class MLSWelcomeDTO(
//            @JsonProperty("qualified_conversation") val qualifiedConversation: ConversationId,
//            @JsonProperty("qualified_from") val qualifiedFrom: UserId,
//            @JsonProperty("data") val message: String,
//            @JsonProperty("from") val from: String
//        ) : Conversation()
//
//    }
//
//
//    sealed class Team : EventContentDTO() {
//
//        @JsonProperty("team.update")
//        data class Update(
//            @JsonProperty("data") val teamUpdate: TeamUpdateData,
//            @JsonProperty("team") val teamId: TeamId,
//            val time: String,
//        ) : Team()
//
//
//        @JsonProperty("team.member-join")
//        data class MemberJoin(
//            @JsonProperty("data") val teamMember: TeamMemberIdData,
//            @JsonProperty("team") val teamId: TeamId,
//            val time: String,
//        ) : Team()
//
//
//        @JsonProperty("team.member-leave")
//        data class MemberLeave(
//            @JsonProperty("data") val teamMember: TeamMemberIdData,
//            @JsonProperty("team") val teamId: TeamId,
//            val time: String,
//        ) : Team()
//
//
//        @JsonProperty("team.member-update")
//        data class MemberUpdate(
//            @JsonProperty("data") val permissionsResponse: PermissionsData,
//            @JsonProperty("team") val teamId: TeamId,
//            val time: String,
//        ) : Team()
//
//    }
//
//
//    sealed class User : EventContentDTO() {
//
//
//        @JsonProperty("user.client-add")
//        data class NewClientDTO(
//            @JsonProperty("client") val client: NewClientEventData,
//        ) : User()
//
//
//        @JsonProperty("user.client-remove")
//        data class ClientRemoveDTO(
//            @JsonProperty("client") val client: RemoveClientEventData,
//        ) : User()
//
//        // TODO user.properties-set
//
//        // TODO user.properties-delete
//
//
//        @JsonProperty("user.update")
//        data class UpdateDTO(
//            @JsonProperty("user") val userData: UserUpdateEventData,
//        ) : User()
//
//        // TODO user.identity-remove
//
//
//        @JsonProperty("user.connection")
//        data class NewConnectionDTO(
//            @JsonProperty("connection") val connection: ConnectionDTO,
//        ) : User()
//
//        // TODO user.push-remove
//
//
//        @JsonProperty("user.delete")
//        data class UserDeleteDTO(
//            @JsonProperty("id") val id: String,
//            @JsonProperty("qualified_id") val userId: UserId
//        ) : User()
//    }
//
//
//    sealed class FeatureConfig : EventContentDTO() {
//        (with = JsonCorrectingSerializer::class)
//        @JsonProperty("feature-config.update")
//        data class FeatureConfigUpdatedDTO(
//            @JsonProperty("data") val data: FeatureConfigData,
//        ) : FeatureConfig()
//    }
//
//
//    @JsonProperty("unknown")
//    object Unknown : EventContentDTO()
//}

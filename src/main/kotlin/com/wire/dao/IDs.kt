package com.wire.dao

import java.util.UUID

typealias TeamId = UUID

typealias UserId = QualifiedId
typealias NonQualifiedUserId = UUID
typealias Domain = String
typealias NonQualifiedHandle = String
typealias Handle = QualifiedHandle

typealias ClientId = String

typealias ConversationId = QualifiedId
typealias NonQualifiedConversationId = UUID

typealias AssetId = QualifiedId
typealias AssetKey = String

typealias MLSPublicKey = String
typealias KeyPackage = String

data class QualifiedId(val domain: Domain, val id: NonQualifiedUserId)
data class QualifiedHandle(val domain: Domain, val handle: NonQualifiedHandle)

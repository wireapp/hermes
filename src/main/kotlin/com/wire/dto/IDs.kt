package com.wire.dto

typealias ConversationId = QualifiedId
typealias NonQualifiedConversationId = String
typealias UserId = QualifiedId
typealias NonQualifiedUserId = String
typealias TeamId = String
typealias AssetId = QualifiedId
typealias AssetKey = String
typealias MLSPublicKey = String
typealias KeyPackage = String

data class QualifiedId(val id: String, val domain: String)

package com.wire.dao.model

object Users : ManagedTable("users") {
    val handle = text("handle").uniqueIndex()

    val teamId = uuid("team_id").references(Teams.id).nullable()

    val email = text("email").nullable().uniqueIndex()

    val passwordHash = text("password_hash")
}

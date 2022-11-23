package com.wire.dao.model

object Conversations : ManagedTable("conversations") {
    val name = text("name")
}

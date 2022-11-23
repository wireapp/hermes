package com.wire.dao.model

object Teams : ManagedTable("teams") {
    val name = text("name")
}

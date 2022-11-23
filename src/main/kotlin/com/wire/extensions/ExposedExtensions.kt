package com.wire.extensions

import com.wire.dao.Domain
import com.wire.dao.QualifiedId
import com.wire.dao.model.ManagedTable
import org.jetbrains.exposed.sql.AbstractQuery
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

/**
 * See [Query.empty].
 */
fun Query.isNotEmpty() = !this.empty()

/**
 * See [Query.empty].
 */
fun Query.isEmpty() = this.empty()

/**
 * Uses [AbstractQuery.limit] and selects first result row,
 * if no row is present, returns null.
 */
fun <T : AbstractQuery<T>> AbstractQuery<T>.getOne(): ResultRow? = limit(1).firstOrNull()

/**
 * Adds where clause for [ManagedTable.domain].
 */
fun ManagedTable.selectForDomain(domain: Domain) = select { this@selectForDomain.domain eq domain }

fun ResultRow.toQualifiedId(table: ManagedTable) = QualifiedId(this[table.domain], this[table.id])

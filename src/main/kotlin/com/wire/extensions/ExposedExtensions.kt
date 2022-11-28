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

/**
 * Maps one-to-many relationships, where we expect only a single "one" part of the row.
 *
 * Executes [oneMapper] to first row and then only [manyMapper] for the rest.
 * Returns a pair of (one, many) if the [ResultRow] is not empty, otherwise returns null.
 */
inline fun <reified O, reified M> Query.getOneToMany(
    oneMapper: (ResultRow) -> O,
    manyMapper: (one: O, ResultRow) -> M
): Pair<O, List<M>>? {
    val iter = this.iterator()
    if (!iter.hasNext()) return null

    val firstElement = iter.next()
    val one = oneMapper(firstElement)
    val results = mutableListOf(manyMapper(one, firstElement))

    while (iter.hasNext()) {
        results.add(manyMapper(one, iter.next()))
    }
    return Pair(one, results.toList())
}

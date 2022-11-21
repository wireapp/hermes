package com.wire.dao

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource
import org.jetbrains.exposed.sql.Database as ExposedDb

/**
 * Class wrapping [org.jetbrains.exposed.sql.Database].
 */
class Database(private val connectedDb: ExposedDb) {

    companion object {
        /**
         * Connect to the database using provided [DataSource].
         *
         * This method does not check whether it is possible to connect to the database, use [isConnected] for that.
         */
        fun connect(dataSource: DataSource): Database = Database(ExposedDb.connect(dataSource))
    }

    /**
     * Returns true if the app is connected to database.
     */
    fun isConnected() = runCatching {
        // execute simple query to verify whether the db is connected
        // if the transaction throws exception, database is not connected
        transaction { this.connection.isClosed }
    }.isSuccess

    /**
     * Executes query with this database.
     */
    suspend fun <T> query(
        statement: suspend Transaction.() -> T
    ): T = newSuspendedTransaction(Dispatchers.IO, this@Database.connectedDb) { statement() }

    /**
     * Executes async query in the database.
     */
    suspend fun <T> queryAsync(
        statement: suspend Transaction.() -> T
    ): Deferred<T> = suspendedTransactionAsync(Dispatchers.IO, this@Database.connectedDb) { statement() }
}

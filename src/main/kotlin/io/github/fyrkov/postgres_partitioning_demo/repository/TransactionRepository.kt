package io.github.fyrkov.postgres_partitioning_demo.repository

import io.github.fyrkov.postgres_partitioning_demo.domain.Transaction
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Repository
class TransactionRepository(
    private val dsl: DSLContext,
) {
    fun insert(tx: Transaction): Transaction {
        dsl.execute(
            """
      insert into transactions(account_id, tx_id, tx_type, amount)
      values (?, ?, ?, ?)
      """.trimIndent(),
            tx.accountId,
            tx.id,
            tx.txType,
            tx.amount,
        )
        return tx
    }

    fun findAll(accountIds: List<UUID>? = null, limit: Int = 10, offset: Long = 0): List<Transaction> {
        val query = StringBuilder("""
            select account_id, tx_id, tx_type, amount, created_at
            from transactions
        """.trimIndent())

        val params = mutableListOf<Any>()
        if (!accountIds.isNullOrEmpty()) {
            query.append("\nwhere account_id in (")
            query.append(accountIds.joinToString(",") { "?" })
            query.append(")")
            params.addAll(accountIds)
        }

        query.append("\norder by created_at desc, tx_id")
        query.append("\nlimit ? offset ?")
        params.add(limit)
        params.add(offset)

        return dsl.fetch(query.toString(), *params.toTypedArray())
            .map { r -> mapToTransaction(r) }
    }

    fun findById(txId: UUID): Transaction? =
        dsl.fetchOne(
            """
                select account_id, tx_id, tx_type, amount, created_at
                from transactions
                where tx_id = ?
                """.trimIndent(),
            txId
        )
            ?.let { r -> mapToTransaction(r) }

    private fun mapToTransaction(r: Record): Transaction = Transaction(
        id = r.get("tx_id", UUID::class.java),
        accountId = r.get("account_id", UUID::class.java),
        txType = r.get("tx_type", String::class.java),
        amount = r.get("amount", BigDecimal::class.java),
        createdAt = r.get("created_at", Instant::class.java),
    )

}
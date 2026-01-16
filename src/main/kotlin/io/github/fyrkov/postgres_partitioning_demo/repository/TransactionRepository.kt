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

    fun findAllByAccountId(accountId: UUID): List<Transaction> =
        dsl.fetch(
            """
        select account_id, tx_id, tx_type, amount, created_at
        from transactions
        where account_id = ?
        order by created_at desc, tx_id
        """.trimIndent(),
            accountId
        )
            .map { r -> mapToTransaction(r) }

    fun findAll(): List<Transaction> =
        dsl.fetch(
            """
                select account_id, tx_id, tx_type, amount, created_at
                from transactions
                order by created_at desc, tx_id
                """.trimIndent()
        )
            .map { r -> mapToTransaction(r) }
            .sortedByDescending { it.createdAt }

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
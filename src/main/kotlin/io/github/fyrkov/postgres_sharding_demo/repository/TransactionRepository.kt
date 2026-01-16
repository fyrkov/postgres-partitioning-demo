package io.github.fyrkov.postgres_sharding_demo.repository

import io.github.fyrkov.postgres_sharding_demo.domain.Transaction
import io.github.fyrkov.postgres_sharding_demo.domain.TransactionId
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
            tx.id.accountId,
            tx.id.txId,
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

    fun findById(id: TransactionId): Transaction? = findById(id.accountId, id.txId)

    fun findById(accountId: UUID, txId: UUID): Transaction? =
        dsl.fetchOne(
            """
                select account_id, tx_id, tx_type, amount, created_at
                from transactions
                where account_id = ? and tx_id = ?
                """.trimIndent(),
            accountId,
            txId
        )
            ?.let { r -> mapToTransaction(r) }

    private fun mapToTransaction(r: Record): Transaction = Transaction(
        id = TransactionId(
            accountId = r.get("account_id", UUID::class.java),
            txId = r.get("tx_id", UUID::class.java),
        ),
        txType = r.get("tx_type", String::class.java),
        amount = r.get("amount", BigDecimal::class.java),
        createdAt = r.get("created_at", Instant::class.java),
    )

}
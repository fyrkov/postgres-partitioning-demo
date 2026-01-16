package io.github.fyrkov.postgres_partitioning_demo.controller

import io.github.fyrkov.postgres_partitioning_demo.domain.Transaction
import io.github.fyrkov.postgres_partitioning_demo.domain.TransactionId
import io.github.fyrkov.postgres_partitioning_demo.repository.TransactionRepository
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionRepository: TransactionRepository
) {
    @PostMapping
    fun createTransaction(
        @RequestBody request: TransactionRequest
    ): Transaction {
        val accountId = request.accountId ?: throw IllegalArgumentException("accountId is required")
        val transaction = Transaction(
            id = TransactionId(
                accountId = accountId,
                txId = UUID.randomUUID()
            ),
            txType = request.txType,
            amount = request.amount
        )
        return transactionRepository.insert(transaction)
    }

    @GetMapping
    fun listTransactions(@RequestParam(required = false) accountIds: List<UUID>?): List<Transaction> {
        return if (accountIds.isNullOrEmpty()) {
            transactionRepository.findAll()
        } else {
            accountIds.flatMap { transactionRepository.findAllByAccountId(it) }
                .sortedByDescending { it.createdAt }
        }
    }
}

data class TransactionRequest(
    val accountId: UUID? = null,
    val txType: String = "DEPOSIT",
    val amount: BigDecimal = BigDecimal.ZERO
)

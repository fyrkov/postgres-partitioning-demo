package io.github.fyrkov.postgres_partitioning_demo.controller

import io.github.fyrkov.postgres_partitioning_demo.domain.Transaction
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
            id = UUID.randomUUID(),
            accountId = accountId,
            txType = request.txType,
            amount = request.amount
        )
        return transactionRepository.insert(transaction)
    }

    @GetMapping
    fun listTransactions(
        @RequestParam(required = false) accountIds: List<UUID>? = null,
        @RequestParam(defaultValue = "10") limit: Int = 10,
        @RequestParam(defaultValue = "0") offset: Long = 0
    ): List<Transaction> {
        return transactionRepository.findAll(accountIds, limit, offset)
    }
}

data class TransactionRequest(
    val accountId: UUID? = null,
    val txType: String = "DEPOSIT",
    val amount: BigDecimal = BigDecimal.ZERO
)

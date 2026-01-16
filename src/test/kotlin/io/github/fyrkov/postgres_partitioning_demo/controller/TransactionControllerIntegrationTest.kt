package io.github.fyrkov.postgres_partitioning_demo.controller

import io.github.fyrkov.postgres_partitioning_demo.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class TransactionControllerIntegrationTest(
    @Autowired val transactionController: TransactionController,
    @Autowired val accountController: AccountController,
) : AbstractIntegrationTest() {

    @Test
    fun `should create and list transactions`() {
        // Given
        val account = accountController.createAccount(AccountRequest("John", "Doe", BigDecimal.ZERO))
        val accountId = account.accountId
        val request = TransactionRequest(
            accountId = accountId,
            txType = "DEPOSIT",
            amount = BigDecimal("100.50")
        )

        // When
        val created = transactionController.createTransaction(request)

        // Then
        assertEquals(accountId, created.id.accountId)
        assertEquals("DEPOSIT", created.txType)
        assertEquals(BigDecimal("100.50"), created.amount)

        // And When
        val transactions = transactionController.listTransactions(listOf(accountId))

        // Then
        assertTrue(transactions.isNotEmpty())
        assertTrue(transactions.any { it.id.txId == created.id.txId })

        // Test Global Transactions (all)
        val allTransactions = transactionController.listTransactions(null)
        assertTrue(allTransactions.isNotEmpty())
        assertTrue(allTransactions.any { it.id.txId == created.id.txId })
    }
}

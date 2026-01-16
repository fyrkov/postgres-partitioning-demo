package io.github.fyrkov.postgres_partitioning_demo.repository

import io.github.fyrkov.postgres_partitioning_demo.AbstractIntegrationTest
import io.github.fyrkov.postgres_partitioning_demo.domain.Account
import io.github.fyrkov.postgres_partitioning_demo.domain.Transaction
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.*

@SpringBootTest
class TransactionRepositoryIntegrationTest(
    @Autowired var transactionRepository: TransactionRepository,
    @Autowired var accountRepository: AccountRepository,
) : AbstractIntegrationTest() {

    @Test
    fun `should store transactions for same account`() {
        // Given
        val accountId = UUID.randomUUID()
        accountRepository.save(Account(accountId, "John", "Doe", BigDecimal.ZERO))

        val tx1 = Transaction(
            id = UUID.randomUUID(),
            accountId = accountId,
            txType = "DEPOSIT",
            amount = BigDecimal("100.00")
        )
        val tx2 = Transaction(
            id = UUID.randomUUID(),
            accountId = accountId,
            txType = "WITHDRAWAL",
            amount = BigDecimal("50.00")
        )

        // When
        transactionRepository.insert(tx1)
        transactionRepository.insert(tx2)

        // Then
        val allTransactions = transactionRepository.findAll(listOf(accountId))
        assertTrue(allTransactions.any { it.id == tx1.id })
        assertTrue(allTransactions.any { it.id == tx2.id })
    }
}
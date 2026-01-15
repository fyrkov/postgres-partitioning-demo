package io.github.fyrkov.postgres_sharding_demo.repository

import io.github.fyrkov.postgres_sharding_demo.AbstractIntegrationTest
import io.github.fyrkov.postgres_sharding_demo.domain.Account
import io.github.fyrkov.postgres_sharding_demo.domain.Transaction
import io.github.fyrkov.postgres_sharding_demo.domain.TransactionId
import org.junit.jupiter.api.Assertions.assertEquals
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
            id = TransactionId(accountId, UUID.randomUUID()),
            txType = "DEPOSIT",
            amount = BigDecimal("100.00")
        )
        val tx2 = Transaction(
            id = TransactionId(accountId, UUID.randomUUID()),
            txType = "WITHDRAWAL",
            amount = BigDecimal("50.00")
        )

        // When
        transactionRepository.insert(tx1)
        transactionRepository.insert(tx2)

        // Then
        val allTransactions = transactionRepository.findAllByAccountId(accountId)
        assertTrue(allTransactions.any { it.id == tx1.id })
        assertTrue(allTransactions.any { it.id == tx2.id })
    }

    @Test
    fun `should store transactions in different shards`() {
        // Given
        val accountId1: UUID = generateSequence { UUID.randomUUID() }
            .first { it.hashCode() % 2 == 0 }
        accountRepository.save(Account(accountId1, "Acc1", "Test", BigDecimal.ZERO))

        val accountId2: UUID = generateSequence { UUID.randomUUID() }
            .first { it.hashCode() % 2 == 1 }
        accountRepository.save(Account(accountId2, "Acc2", "Test", BigDecimal.ZERO))


        val tx1 = Transaction(
            id = TransactionId(accountId1, UUID.randomUUID()),
            txType = "DEPOSIT",
            amount = BigDecimal("90.00")
        )
        val tx2 = Transaction(
            id = TransactionId(accountId2, UUID.randomUUID()),
            txType = "DEPOSIT",
            amount = BigDecimal("115.00")
        )

        // When
        transactionRepository.insert(tx1)
        transactionRepository.insert(tx2)

        // Then
        assertEquals(transactionRepository.findById(tx1.id)!!.id, tx1.id)
        assertEquals(transactionRepository.findById(tx2.id)!!.id, tx2.id)
    }
}
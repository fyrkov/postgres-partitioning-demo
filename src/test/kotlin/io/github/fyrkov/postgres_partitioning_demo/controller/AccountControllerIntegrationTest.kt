package io.github.fyrkov.postgres_partitioning_demo.controller

import io.github.fyrkov.postgres_partitioning_demo.AbstractIntegrationTest
import io.github.fyrkov.postgres_partitioning_demo.repository.AccountRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class AccountControllerIntegrationTest(
    @Autowired val accountController: AccountController,
    @Autowired val accountRepository: AccountRepository,
) : AbstractIntegrationTest() {

    @Test
    fun `should create account`() {
        val request = AccountRequest("John", "Doe", BigDecimal("100.00"))
        val result = accountController.createAccount(request)

        assertNotNull(result.accountId)
        assertEquals("John", result.firstName)
        assertEquals("Doe", result.lastName)
        assertEquals(0, BigDecimal("100.00").compareTo(result.balance))
        assertNotNull(accountRepository.findById(result.accountId))
    }

    @Test
    fun `should list accounts`() {
        // Given
        val request = AccountRequest("Jane", "Doe", BigDecimal("50.00"))
        val account = accountController.createAccount(request)

        // When
        val accounts = accountController.listAccounts()

        // Then
        assertTrue(accounts.isNotEmpty())
        assertTrue(accounts.any { it.accountId == account.accountId })
    }
}

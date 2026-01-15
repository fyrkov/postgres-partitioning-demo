package io.github.fyrkov.postgres_sharding_demo.controller

import io.github.fyrkov.postgres_sharding_demo.domain.Account
import io.github.fyrkov.postgres_sharding_demo.repository.AccountRepository
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountRepository: AccountRepository
) {
    @PostMapping
    fun createAccount(@RequestBody request: AccountRequest): Account {
        val account = Account(
            accountId = UUID.randomUUID(),
            firstName = request.firstName,
            lastName = request.lastName,
            balance = request.balance
        )
        return accountRepository.save(account)
    }

    @GetMapping
    fun listAccounts(): List<Account> {
        return accountRepository.findAll()
    }
}

data class AccountRequest(
    val firstName: String,
    val lastName: String,
    val balance: BigDecimal = BigDecimal.ZERO
)

package io.github.fyrkov.postgres_sharding_demo.repository

import io.github.fyrkov.postgres_sharding_demo.domain.Account
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Repository
class AccountRepository(
    private val shardsRouter: ShardsRouter,
) {
    fun save(account: Account): Account {
        shardsRouter.shardFor(account.accountId).execute(
            "insert into accounts(account_id, first_name, last_name, balance) values (?, ?, ?, ?)",
            account.accountId,
            account.firstName,
            account.lastName,
            account.balance
        )
        return account
    }

    fun findById(accountId: UUID): Account? =
        shardsRouter.shardFor(accountId)
            .fetchOne(
                "select * from accounts where account_id = ?",
                accountId
            )
            ?.let { r -> mapRecordToAccount(r) }

    fun findAll(): List<Account> =
        shardsRouter.allShards().flatMap { dsl ->
            dsl.fetch("select * from accounts")
                .map { r -> mapRecordToAccount(r) }
        }

    private fun mapRecordToAccount(r: Record): Account = Account(
        accountId = r.get("account_id", UUID::class.java),
        firstName = r.get("first_name", String::class.java),
        lastName = r.get("last_name", String::class.java),
        balance = r.get("balance", BigDecimal::class.java),
        createdAt = r.get("created_at", Instant::class.java),
    )
}

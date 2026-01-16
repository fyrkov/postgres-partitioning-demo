package io.github.fyrkov.postgres_partitioning_demo.domain

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Account(
    val accountId: UUID,
    val firstName: String,
    val lastName: String,
    val balance: BigDecimal,
    val createdAt: Instant? = null,
)
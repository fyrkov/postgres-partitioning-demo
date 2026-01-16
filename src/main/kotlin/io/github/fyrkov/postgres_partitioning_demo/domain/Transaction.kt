package io.github.fyrkov.postgres_partitioning_demo.domain

import java.util.UUID
import java.math.BigDecimal
import java.time.Instant

data class Transaction(
    val id: UUID,
    val accountId: UUID,
    val txType: String,
    val amount: BigDecimal,
    val createdAt: Instant? = null,
)
package io.github.fyrkov.postgres_partitioning_demo.domain

import java.math.BigDecimal
import java.time.Instant

data class Transaction(
    val id: TransactionId,
    val txType: String,
    val amount: BigDecimal,
    val createdAt: Instant? = null,
)
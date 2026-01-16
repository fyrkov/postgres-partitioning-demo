package io.github.fyrkov.postgres_partitioning_demo.domain

import java.util.UUID

data class TransactionId(
    val accountId: UUID,
    val txId: UUID,
)
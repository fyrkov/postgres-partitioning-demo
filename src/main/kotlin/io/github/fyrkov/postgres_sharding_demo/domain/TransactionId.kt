package io.github.fyrkov.postgres_sharding_demo.domain

import java.util.UUID

data class TransactionId(
    val accountId: UUID,
    val txId: UUID,
)
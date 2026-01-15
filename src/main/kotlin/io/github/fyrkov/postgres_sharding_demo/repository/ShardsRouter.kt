package io.github.fyrkov.postgres_sharding_demo.repository

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.*

@Component
class ShardsRouter(
    @Qualifier("dslShard1") private val dsl1: DSLContext,
    @Qualifier("dslShard2") private val dsl2: DSLContext,
) {
    fun shardFor(accountId: UUID): DSLContext =
        if (accountId.hashCode() % 2 == 0) {
            dsl1.also { println("Routing to shard 1 for account $accountId") }
        } else {
            dsl2.also { println("Routing to shard 2 for account $accountId") }
        }

    fun allShards(): List<DSLContext> = listOf(dsl1, dsl2)
}
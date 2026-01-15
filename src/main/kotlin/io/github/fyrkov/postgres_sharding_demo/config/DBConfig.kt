package io.github.fyrkov.postgres_sharding_demo.config

import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DBConfig {

    @Bean
    fun shard1DataSource(
        @Value("\${postgres.shard1.datasource.url}") url: String,
        @Value("\${postgres.shard1.datasource.username}") username: String,
        @Value("\${postgres.shard1.datasource.password}") password: String,
    ): DataSource = DataSourceBuilder.create().url(url).username(username).password(password).build()

    @Bean
    fun shard2DataSource(
        @Value("\${postgres.shard2.datasource.url}") url: String,
        @Value("\${postgres.shard2.datasource.username}") username: String,
        @Value("\${postgres.shard2.datasource.password}") password: String,
    ): DataSource = DataSourceBuilder.create().url(url).username(username).password(password).build()

    @Bean("dslShard1")
    fun dslShard1(@Qualifier("shard1DataSource") ds: DataSource): DSLContext =
        DSL.using(ds, SQLDialect.POSTGRES)

    @Bean("dslShard2")
    fun dslShard2(@Qualifier("shard2DataSource") ds: DataSource): DSLContext =
        DSL.using(ds, SQLDialect.POSTGRES)

    @Bean
    fun migrate(
        shard1DataSource: DataSource,
        shard2DataSource: DataSource,
    ) = ApplicationRunner {
        listOf(shard1DataSource, shard2DataSource).forEach { ds ->
            Flyway.configure()
                .dataSource(ds)
                .baselineOnMigrate(true)
                .load()
                .migrate()
        }
    }
}
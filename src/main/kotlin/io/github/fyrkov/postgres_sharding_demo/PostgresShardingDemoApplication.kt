package io.github.fyrkov.postgres_sharding_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostgresShardingDemoApplication

fun main(args: Array<String>) {
	runApplication<PostgresShardingDemoApplication>(*args)
}

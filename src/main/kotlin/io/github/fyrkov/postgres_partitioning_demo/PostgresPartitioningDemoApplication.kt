package io.github.fyrkov.postgres_partitioning_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostgresPartitioningDemoApplication

fun main(args: Array<String>) {
	runApplication<PostgresPartitioningDemoApplication>(*args)
}

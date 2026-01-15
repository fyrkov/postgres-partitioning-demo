package io.github.fyrkov.postgres_sharding_demo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
abstract class AbstractIntegrationTest {

    companion object {
        private val image = DockerImageName.parse("postgres:17")

        @JvmStatic
        val shard1: PostgreSQLContainer = PostgreSQLContainer(image)
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("secret")

        @JvmStatic
        val shard2: PostgreSQLContainer = PostgreSQLContainer(image)
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("secret")

        init {
            shard1.start()
            shard2.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(registry: DynamicPropertyRegistry) {
            registry.add("postgres.shard1.datasource.url") { shard1.jdbcUrl }
            registry.add("postgres.shard1.datasource.username") { shard1.username }
            registry.add("postgres.shard1.datasource.password") { shard1.password }

            registry.add("postgres.shard2.datasource.url") { shard2.jdbcUrl }
            registry.add("postgres.shard2.datasource.username") { shard2.username }
            registry.add("postgres.shard2.datasource.password") { shard2.password }
        }
    }
}

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
        val postgresContainer: PostgreSQLContainer = PostgreSQLContainer(image)
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("secret")

       init {
            postgresContainer.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
        }
    }
}

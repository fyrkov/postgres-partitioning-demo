package io.github.fyrkov.postgres_partitioning_demo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
abstract class AbstractIntegrationTest {

    companion object {
        private val image = DockerImageName.parse("postgres:17")

        @JvmStatic
        @ServiceConnection
        val postgresContainer: PostgreSQLContainer = PostgreSQLContainer(image)
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("secret")
    }
}

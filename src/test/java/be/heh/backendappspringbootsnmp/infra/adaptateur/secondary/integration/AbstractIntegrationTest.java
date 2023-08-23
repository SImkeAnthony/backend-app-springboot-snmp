package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.integration;

import org.flywaydb.core.Flyway;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractIntegrationTest {
    protected static PostgreSQLContainer POSTGRES_SQL_CONTAINER;

    static {
        POSTGRES_SQL_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.2-alpine"));
        POSTGRES_SQL_CONTAINER.start();

        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRES_SQL_CONTAINER.getJdbcUrl(), POSTGRES_SQL_CONTAINER.getUsername(), POSTGRES_SQL_CONTAINER.getPassword())
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
    }
    @DynamicPropertySource
    static void overrideTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_SQL_CONTAINER::getPassword);
    }
}

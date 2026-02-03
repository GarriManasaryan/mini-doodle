package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class DatabaseBaseTest {

    private static final PostgreSQLContainer<?> POSTGRES;
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")
            .ignoreIfMissing()
            .load();

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName(getEnv("PG_DATABASE_NAME", "meetingscheduler"))
                .withUsername(getEnv("PG_USERNAME", "1"))
                .withPassword(getEnv("PG_PASSWORD", "postgres"));
        POSTGRES.start();
    }

    private static String getEnv(String key, String defaultValue) {
        var value = dotenv.get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");

        registry.add("spring.flyway.enabled", () -> true);
    }
}

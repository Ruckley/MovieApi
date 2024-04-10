package org.bb.app;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public class TestContainerConfig {

    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest");

    public static void initialize(DynamicPropertyRegistry registry, JdbcDatabaseContainer<?> container) {
        mysqlContainer.start();
        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + container.getHost() + ":" + container.getFirstMappedPort() + "/" + container.getDatabaseName());
        registry.add("spring.r2dbc.username", container::getUsername);
        registry.add("spring.r2dbc.password", container::getPassword);
    }
}
package org.bb.app;

import org.testcontainers.containers.MySQLContainer;

public class TestMySqlContainer extends MySQLContainer<TestMySqlContainer> {
    private static final String IMAGE_VERSION = "latest";
    public static final String DATABASE_NAME = "movie_db";
    private static TestMySqlContainer container;

    private TestMySqlContainer() {
        super(IMAGE_VERSION);
    }

    public static TestMySqlContainer getInstance() {
        if (container == null) {
            container = new TestMySqlContainer().withDatabaseName(DATABASE_NAME);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
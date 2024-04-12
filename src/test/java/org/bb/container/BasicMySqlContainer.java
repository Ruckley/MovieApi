package org.bb.container;

import org.testcontainers.containers.MySQLContainer;

public class BasicMySqlContainer extends MySQLContainer<BasicMySqlContainer> {

    private static BasicMySqlContainer container;
    private static final String IMAGE_VERSION = "mysql:latest";
    private BasicMySqlContainer() {
        super(IMAGE_VERSION);
    }

    public static BasicMySqlContainer getRunningInstance() {
        if (container == null) {
            container = new BasicMySqlContainer();
            container.start();
        } else if(!container.isRunning()) container.start();
        return container;
    }

}
package org.bb.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;


@Component
public class DbConnectionProvider {
    private static final Logger LOGGER = Logger.getLogger(DbConnectionProvider.class.getName());

    private final String url;
    private final String username;
    private final String password;


    public DbConnectionProvider(
            @Value("${app.db.url}") String url,
            @Value("${app.db.username}")String username,
            @Value("${app.db.password}")String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        LOGGER.info(
                "Dbconnector URL: " + url + "\n" +
                "Dbconnector username: " + username + "\n"
        );
    }

    Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

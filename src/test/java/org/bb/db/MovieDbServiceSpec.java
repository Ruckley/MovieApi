package org.bb.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

class CustomerServiceTest {

    static MySQLContainer<?> mysql = new MySQLContainer<>(
            "mysql:latest"  //!! replace with config
    );

    MySqlMovieDbService movieDbService;

    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
    }

    @BeforeEach
    void setUp() {
        DbConnectionProvider connectionProvider = new DbConnectionProvider(
                mysql.getJdbcUrl(),
                mysql.getUsername(),
                mysql.getPassword()
        );
        movieDbService = new MySqlMovieDbService(connectionProvider);
    }

    @Test
    void shouldGetCustomers() {
//        movieDbService.createCustomer(new Customer(1L, "George"));
//        movieDbService.createCustomer(new Customer(2L, "John"));
//
//        List<Customer> customers = movieDbService.getAllCustomers();
//        assertEquals(2, customers.size());
    }
}

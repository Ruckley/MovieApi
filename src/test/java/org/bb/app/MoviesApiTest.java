package org.bb.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MoviesApiTest {


    @Autowired
    AwardsRepository awardsRepository;
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
            "mysql:latest"  //!! replace with config
    );

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        mysqlContainer.start();
        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + mysqlContainer.getHost() + ":" + mysqlContainer.getFirstMappedPort() + "/" + mysqlContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", () -> mysqlContainer.getUsername());
        registry.add("spring.r2dbc.password", () -> mysqlContainer.getPassword());
    }



    @Test
    void shouldGetCustomers() {
        System.out.println("WTF IS GOING ON");
        Mono<Award> x = awardsRepository.save(new Award("cat", "nom", "adInf", true, 1990));
        Award result = x.block();

        System.out.println("result: " + result);
    }
}

package org.bb.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bb.app.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Replace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
//@ComponentScan("org.bb.app")
//@SpringBootTest(classes = {AwardsRepository.class, AwardsService.class})
@SpringBootTest//(classes = {Controller.class, AwardsRepository.class, AwardsService.class})
@ComponentScan("org.bb.app")
class MoviesApiSpec2 {

//    static MySQLContainer<?> mysql = new MySQLContainer<>(
//            "mysql:latest"  //!! replace with config
//    );

//    @DynamicPropertySource
//    static void setDynamicProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.r2dbc.url", () -> mysql.getJdbcUrl());
//        registry.add("spring.r2dbc.username", () -> mysql.getUsername());
//        registry.add("spring.r2dbc.password", () -> mysql.getPassword());
//    }

//    @Autowired
//    AwardsRepository awardsRepository;

//    @BeforeAll
//    static void beforeAll() {
//        mysql.start();
//    }
//
//    @AfterAll
//    static void afterAll() {
//        mysql.stop();
//    }

//    @Test
//    void shouldGetCustomers() {
//        awardsRepository.save(new Award("cat", "nom", "adInf", true, 1990));
//    }

    @Test
    void contextLoads() {
    }
}

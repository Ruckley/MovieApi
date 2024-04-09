package org.bb.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;


//public class Main {
//    public static void main(String[] args) {
//
//
//        ApplicationContext configContext = new AnnotationConfigApplicationContext(AppConfig.class);
////        DbConnectionProvider dbConnectionProvider = context.getBean(DbConnectionProvider.class);
////        MySqlMovieDbService movieService = new MySqlMovieDbService(dbConnectionProvider);
//
//    }
//
//    @Bean
//    public Controller Controller() {
//        return new Controller();
//    }
//}

@EnableWebFlux
@EnableR2dbcRepositories
@SpringBootApplication
@ComponentScan(basePackages = "org.bb")
public class MoviesApi {

    public static void main(String[] args) {
        SpringApplication.run(MoviesApi.class, args);
    }

}
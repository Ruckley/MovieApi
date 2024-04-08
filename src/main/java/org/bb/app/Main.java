package org.bb.app;

import org.bb.api.Controller;
import org.bb.config.AppConfig;
import org.bb.db.DbConnectionProvider;
import org.bb.db.MySqlMovieDbService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;


public class Main {
    public static void main(String[] args) {


        ApplicationContext configContext = new AnnotationConfigApplicationContext(AppConfig.class);
//        DbConnectionProvider dbConnectionProvider = context.getBean(DbConnectionProvider.class);
//        MySqlMovieDbService movieService = new MySqlMovieDbService(dbConnectionProvider);

    }

    @Bean
    public Controller Controller() {
        return new Controller();
    }
}
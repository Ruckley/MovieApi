package org.bb.app.config;

import org.bb.app.security.ApiTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class Config {

    @Autowired
    private ApiTokenFilter apiTokenFilter;

    @Bean
    public WebFilter webFilter() {
        return apiTokenFilter;
    }
}

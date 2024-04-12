package org.bb.app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ApiTokenFilter implements WebFilter {

    @Value("${api.token}")
    private String apiToken;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String requestToken = exchange.getRequest().getQueryParams().getFirst("apikey");

        if (apiToken.equals(requestToken)) {
            return chain.filter(exchange);
        } else {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
            return response.writeWith(Mono.just(response.bufferFactory().wrap("Unauthorized".getBytes())));

        }
    }
}
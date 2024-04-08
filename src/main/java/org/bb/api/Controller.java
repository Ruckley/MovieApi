package org.bb.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

    @GetMapping("/healthCheck")
    public Mono<ResponseEntity<String>> healthcheck() {
        return Mono.just(ResponseEntity.ok("OK"));
    }
}

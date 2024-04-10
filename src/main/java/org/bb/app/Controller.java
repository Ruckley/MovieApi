package org.bb.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/movie_api")
public class Controller {
    @Autowired
    AwardsService movieService;

    @GetMapping("/healthCheck")
    public Mono<ResponseEntity<String>> healthCheck() {
        return Mono.just(ResponseEntity.ok().build());
    }
    @GetMapping("/best_picture_winner")
    public Mono<ResponseEntity<String>> wonBestPicture(@RequestParam(required = false, name = "t") String title) {

        Mono<String> result = movieService.bestPictureWinner(title);

        return result.map(ResponseEntity::ok)
                .onErrorResume(this::handleError);
    }

    private Mono<ResponseEntity<String>> handleError(Throwable error) {
        System.err.println("Error encountered: " + error.getMessage());
        return Mono.just(ResponseEntity
                .status(500)
                .body("Unexpected Error Occurred"));
    }
}

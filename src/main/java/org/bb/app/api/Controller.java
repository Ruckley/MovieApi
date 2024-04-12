package org.bb.app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movie_api")
public class Controller {
    @Autowired
    private MoviesService movieService;

    @GetMapping("/healthCheck")
    public Mono<ResponseEntity<String>> healthCheck() {
        return Mono.just(ResponseEntity.ok().build());
    }
    @GetMapping("/best_picture_winner")
    public Mono<ResponseEntity<String>> wonBestPicture(@RequestParam(required = true, name = "t") String title) {

        Mono<String> result = movieService.bestPictureWinner(title);

        return result.map(ResponseEntity::ok)
                .onErrorResume(this::handleError);
    }

    @PostMapping("/rate_movie")
    public Mono<ResponseEntity<String>> rateMovie(
            @RequestParam(required = true, name = "t") String title,
            @RequestParam(required = false, name = "y") String year,
            @RequestParam(required = true, name = "r") int rating) {
        if (rating < 0 || rating > 10) {
            return Mono.just(ResponseEntity.badRequest().body("Rating must be between 0 and 10"));
        }
        Mono<String> result;
        if (year != null && !year.isEmpty()) {
            int yearInt = Integer.parseInt(year);
            result = movieService.updateRating(title, yearInt, rating);
        } else {
            result = movieService.updateRating(title, rating);
        }

        return result.map(ResponseEntity::ok)
                .onErrorResume(this::handleError);
    }

    @GetMapping("/top_rated")
    public Mono<ResponseEntity<String>> topRated() {
        Mono<String> result = movieService.getTop10Movies();
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

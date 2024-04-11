package org.bb.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MoviesService {
    @Autowired
    private AwardsRepository awardsRepository;

    @Autowired
    private MoviesRepository moviesRepository;


    DecimalFormat df = new DecimalFormat("#.##");
    private final String NO_RESULT_RESPONSE_JSON = """
            {"Response":"False","Error":"Movie not found!"}
            """;

    private static final Logger LOGGER = Logger.getLogger(MoviesService.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();

    public Mono<String> bestPictureWinner(String title) {
        //based on OMDb API

        return findAwardByMovieTitle(title)
                .flatMap(award -> {
                    ObjectNode jsonNode = createBestPictureWinnerJson(award);
                    return createBestPictureWinnerJsonString(jsonNode, award);
                })
                .switchIfEmpty(Mono.just(NO_RESULT_RESPONSE_JSON));
    }

    public Mono<Award> findAwardByMovieTitle(String title) {
        //Movie title is only included in "Best Picture" entries
        return awardsRepository.findFirstByCategoryAndNomineeContaining("Best Picture", title);
    }

    public Mono<String> updateRating(String title, int rating) {
        return updateRatingLogic(moviesRepository.findFirstByTitleIgnoreCase(title), rating);
    }

    public Mono<String> updateRating(String title, int year, int rating) {
        return updateRatingLogic(moviesRepository.findFirstByTitleIgnoreCaseAndYear(title, year), rating);
    }

    private Mono<String> updateRatingLogic(Mono<Movie> movieMono, int rating) {
        return movieMono.flatMap(movie -> {
            float newRating;
            if (movie.getNumRatings() == 0) {
                newRating = (float) rating;
            } else {
                float existingNumRatings = (float) movie.getNumRatings();
                float totalRating = movie.getAvRating() * existingNumRatings + (float) rating;
                newRating = Float.parseFloat(df.format(totalRating / (existingNumRatings + 1)));
            }

            Movie updatedMovie = new Movie(movie.getId(),
                    movie.getTitle(),
                    movie.getYear(),
                    movie.getNumRatings() + 1,
                    newRating);

            // save acts as update when an id is given
            return moviesRepository.save(updatedMovie).flatMap(savedMovie -> {
                return createUpdateRatingJsonString(updatedMovie);
            });
        }).switchIfEmpty(Mono.just(NO_RESULT_RESPONSE_JSON));
    }

    public Mono<String> getTop10Movies() {
        return moviesRepository.findTopNByAvRatingOrderByYearAsc(10).collectList().flatMap(this::createGetTop10Json);
    }


    private ObjectNode createBestPictureWinnerJson(Award award) {
        String won = award.isWon() != null ? (award.isWon() ? "true" : "false") : "unknown";
        ObjectNode rootJsonNode = mapper.createObjectNode();
        rootJsonNode.put("title", award.getNominee());
        rootJsonNode.put("won_best_picture", won);
        return rootJsonNode;
    }

    private ObjectNode createUpdateRatingJson(Movie movie) {
        ObjectNode rootJsonNode = mapper.createObjectNode();
        rootJsonNode.put("title", movie.getTitle());
        rootJsonNode.put("rating", movie.getAvRating());
        rootJsonNode.put("numRatings", movie.getNumRatings());
        return rootJsonNode;
    }

    private Mono<String> createGetTop10Json(List<Movie> movies) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try{
            return Mono.just(mapper.writeValueAsString(movies));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Unable to create Json string from movies list: ", e);
            return Mono.error(e);
        }

    }

    private Mono<String> createBestPictureWinnerJsonString(ObjectNode node, Award award) {
        return createJsonString(node, "bestPictureWinner. Award: " + award);
    }

    private Mono<String> createUpdateRatingJsonString(Movie movie) {
        ObjectNode node = createUpdateRatingJson(movie);
        return createJsonString(node, "createUpdateRatingJson. Movie: " + movie);
    }

    // Allows errors to be bubbled up inside the Mono
    private Mono<String> createJsonString(ObjectNode json, String context) {
        try {
            String s = mapper.writeValueAsString(json);
            return Mono.just(s);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Unable to create Json string. context: " + context, e);
            return Mono.error(e);
        }
    }


}

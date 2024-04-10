package org.bb.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AwardsService {
    @Autowired
    AwardsRepository movieRepository;

    private static final Logger LOGGER = Logger.getLogger(AwardsService.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();

    public Mono<String> bestPictureWinner(String title) {
        //based on OMDb API
        String noResultResponseJson = """
                {"Response":"False","Error":"Movie not found!"}
                """;
        return findAwardByMovieTitle(title)
                .flatMap(award -> {
                    ObjectNode jsonNode = createBestPictureWinnerJson(award);
                    return createBestPictureWinnerJsonString(jsonNode, award);
                })
                .switchIfEmpty(Mono.just(noResultResponseJson));
    }

    public Mono<Award> findAwardByMovieTitle(String title) {
        //Movie title is only included in "Best Picture" entries
        return movieRepository.findFirstByCategoryAndNomineeContaining("Best Picture", title);
    }


    private ObjectNode createBestPictureWinnerJson(Award award) {
        ObjectNode rootJsonNode = mapper.createObjectNode();
        rootJsonNode.put("title", award.getNominee());
        rootJsonNode.put("won_best_picture", award.isWon());
        return rootJsonNode;
    }

    private Mono<String> createBestPictureWinnerJsonString(ObjectNode node, Award award) {
        return createJsonString(node, "bestPictureWinner. Award: " + award);
    }

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

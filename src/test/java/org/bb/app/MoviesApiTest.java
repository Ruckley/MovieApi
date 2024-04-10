package org.bb.app;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(TestContainerConfig.class)
public class MoviesApiTest {


    private static final MySQLContainer<BasicMySqlContainer> mysqlContainer = BasicMySqlContainer.getRunningInstance();
    private static final MovieTestRepo testRepo;

    static {
        try {
            testRepo = new MovieMySqlTestRepo(mysqlContainer);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize testRepo", e);
        }
    }


    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerConnectionHelper.configureDynamicProperties(registry, mysqlContainer);
    }

    @BeforeAll
    static void setupTable() throws SQLException {
        testRepo.createTable();
    }

    @BeforeEach
    void insertTestData() throws SQLException {
        testRepo.insertAwards(testAwards);
    }

    @AfterAll
    static void close() throws SQLException {
        mysqlContainer.close();
    }

    private final String BASE_API = "movie_api";
    private final String BEST_PICTURE_WINNER_REQUEST = BASE_API + "/best_picture_winner";

    Award lotrAward = new Award("Best Picture", "The Lord of the Rings: The Return of the King", "Directed by Peter Jackson, based on J.R.R. Tolkiens novel", true, 2003);
    Award lalaAward = new Award("Best Picture", "La La Land", "Musical romantic comedy-drama film written and directed by Damien Chazelle", false, 2016);

    List<Award> testAwards = List.of(
            new Award("Best Picture", "The Godfather", "Epic crime film directed by Francis Ford Coppola", true, 1972),
            new Award("Best Picture", "The Shawshank Redemption", "Based on the novella by Stephen King", false, 1994),
            new Award("Best Picture", "Schindlers List", "Directed by Steven Spielberg, based on the novel Schindlers Ark", true, 1993),
            lalaAward,
            lotrAward
    );


    @Test
    void bestPictureWinnerCorrectResult() {
        // movie that won best picture
        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=return")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("The Lord of the Rings: The Return of the King")
                .jsonPath("$.won_best_picture").isEqualTo("true");

        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=la+la+land")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("La La Land")
                .jsonPath("$.won_best_picture").isEqualTo("false");

    }

    @Test
    void bestPictureWinnerNoResult() {

        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "t=xxxxxxxxx")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.Response").isEqualTo("False")
                .jsonPath("$.Error").isEqualTo("Movie not found!");

    }
}

class ContainerConnectionHelper {
    public static void configureDynamicProperties(DynamicPropertyRegistry registry, MySQLContainer<?> dbContainer) {

        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + dbContainer.getHost() + ":" + dbContainer.getFirstMappedPort() + "/" + dbContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", dbContainer::getUsername);
        registry.add("spring.r2dbc.password", dbContainer::getPassword);
    }

}


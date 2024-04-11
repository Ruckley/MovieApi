package org.bb.app;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;

import java.sql.SQLException;
import java.util.Comparator;
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

    private final Award lotrAward = new Award("Best Picture", "The Lord of the Rings: The Return of the King", "Directed by Peter Jackson, based on J.R.R. Tolkiens novel", true, 2003);
    private final Award lalaAward = new Award("Best Picture", "La La Land", "Musical romantic comedy-drama film written and directed by Damien Chazelle", false, 2016);
    private final Award godFAward = new Award("Best Picture", "The Godfather", "Epic crime film directed by Francis Ford Coppola", null, 1972);

    private final Award notBPAward = new Award("Some Other Award", "The Shawshank Redemption", "Based on the novella by Stephen King", false, 1994);

    private final List<Award> testAwards = List.of(
            new Award("Best Picture", "Schindlers List", "Directed by Steven Spielberg, based on the novel Schindlers Ark", true, 1993),
            godFAward,
            lalaAward,
            lotrAward,
            notBPAward
    );

    private final Movie noRaintingsMovie = new Movie("The Lord of the Rings: The Return of the King", 2003, 0, null);
    private final Movie preRatedMovie = new Movie("La La Land", 2003, 2, 6f);

    //private final Movie lotrMovie = new Movie("")

    @BeforeAll
    static void setupTable() throws SQLException {

    }

    @BeforeEach
    void insertTestData() throws SQLException {
        testRepo.createAwardsTable();
        testRepo.createMoviesTable();
        testRepo.insertAwards(testAwards);
    }

    @AfterEach
    void dropTable() throws  SQLException {
        testRepo.dropAllTables();
    }

    @AfterAll
    static void close() throws SQLException {
        mysqlContainer.close();
    }

    private final String BASE_API = "movie_api";
    private final String BEST_PICTURE_WINNER_REQUEST = BASE_API + "/best_picture_winner";
    private final String RATE_MOVIE_REQUEST = BASE_API + "/rate_movie";


    private void bestPictureWinnerTestHelper(String title, String expectedTitle, String expectedResult){
        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=" + title)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo(expectedTitle)
                .jsonPath("$.won_best_picture").isEqualTo(expectedResult);
    }


    @Test
    void bestPictureWinnerCorrectResult() {
        // Movie that won Best Picture
        bestPictureWinnerTestHelper(lotrAward.getNominee(), lotrAward.getNominee(), "true");
        // Movie that won Best Picture with partial title
        bestPictureWinnerTestHelper("return", lotrAward.getNominee(), "true");
        // Movie that lost Best Picture
        bestPictureWinnerTestHelper(lalaAward.getNominee(), lalaAward.getNominee(), "false");
        // Using an existing title but the 'won' field is null
        bestPictureWinnerTestHelper("Godfather", godFAward.getNominee(), "unknown");
    }

    @Test
    void bestPictureWinnerNoMatchingResult() {

        // Movie title is in nominee column but the award is not Best Picture
        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=" + notBPAward.getNominee())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.Response").isEqualTo("False")
                .jsonPath("$.Error").isEqualTo("Movie not found!");

        // Movie title is not in table
        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=xxxxxxxxx")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.Response").isEqualTo("False")
                .jsonPath("$.Error").isEqualTo("Movie not found!");
    }


    private void rateMovieTestHelper(String title, int rating, String expectedTitle, float expectedRating, int expectedNumRatings){
        webTestClient.post().uri(RATE_MOVIE_REQUEST + "?t=" + title + "&r=" + rating)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo(expectedTitle)
                .jsonPath("$.rating").isEqualTo(expectedRating)
                .jsonPath("$.numRatings").isEqualTo(expectedNumRatings);
    }
    @Test
    void rateMovieGivesCorrectlyUpdatesTableAndReturnsResult() throws SQLException {
        testRepo.insertMovies(List.of(noRaintingsMovie, preRatedMovie));

        rateMovieTestHelper(noRaintingsMovie.getTitle(), 9, noRaintingsMovie.getTitle(), 9, noRaintingsMovie.getNumRatings() + 1);
        rateMovieTestHelper(preRatedMovie.getTitle(), 5, preRatedMovie.getTitle(), 5.67f, preRatedMovie.getNumRatings() + 1);

    }

    @Test
    void rateMovieGivesWorksCorrectlyOnRepeatedRatings() throws SQLException {
        testRepo.insertMovies(List.of(preRatedMovie));

        rateMovieTestHelper(preRatedMovie.getTitle(), 5, preRatedMovie.getTitle(), 5.67f, preRatedMovie.getNumRatings() + 1);
        rateMovieTestHelper(preRatedMovie.getTitle(), 10, preRatedMovie.getTitle(), 6.75f, preRatedMovie.getNumRatings() + 2);
        rateMovieTestHelper(preRatedMovie.getTitle(), 0, preRatedMovie.getTitle(), 5.4f, preRatedMovie.getNumRatings() + 3);
    }

    @Test
    void rateMovieWorksCorrectlyWithSameNameMovies() throws SQLException {



        Movie dupMovie1990 = new Movie(1,"Moulin Rouge", 1990, 0, null);
        Movie dupMovie1952 = new Movie(2,"Moulin Rouge", 1952, 2, 6f);
        testRepo.insertMovies(List.of(dupMovie1990, dupMovie1952));

        webTestClient.post().uri(RATE_MOVIE_REQUEST + "?t=" + "Moulin Rouge" + "&y=" + 1952 + "&r=" + 5)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Moulin Rouge")
                .jsonPath("$.rating").isEqualTo(5.67f)
                .jsonPath("$.numRatings").isEqualTo(dupMovie1952.getNumRatings() + 1);

        dupMovie1952.setAvRating(5.67f);
        dupMovie1952.setNumRatings(dupMovie1952.getNumRatings() + 1);

        System.out.println("MOVIES");
        for(Movie moive : testRepo.getMovies()){
            System.out.println(moive.toString());
        }

       assertEquals(testRepo.getMovies(), List.of(dupMovie1990, dupMovie1952));
    }



    static class ContainerConnectionHelper {
        public static void configureDynamicProperties(DynamicPropertyRegistry registry, MySQLContainer<?> dbContainer) {

            registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + dbContainer.getHost() + ":" + dbContainer.getFirstMappedPort() + "/" + dbContainer.getDatabaseName());
            registry.add("spring.r2dbc.username", dbContainer::getUsername);
            registry.add("spring.r2dbc.password", dbContainer::getPassword);
        }

    }
}


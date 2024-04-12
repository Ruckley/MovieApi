package org.bb.app.api;

import org.bb.app.model.Award;
import org.bb.app.model.Movie;
import org.bb.container.BasicMySqlContainer;
import org.bb.db.MovieMySqlTestRepo;
import org.bb.db.MovieTestRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoviesApiTest {


    private static final MySQLContainer<BasicMySqlContainer> mysqlContainer = BasicMySqlContainer.getRunningInstance();
    private static final MovieTestRepo testRepo = new MovieMySqlTestRepo(mysqlContainer);

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
    private final String TOP_TEN_REQUEST = BASE_API + "/top_rated";

    //token value should be set in config file
    private final String API_KEY = "&apikey=" + "myToken";



    private void bestPictureWinnerTestHelper(String title, String expectedTitle, String expectedResult){
        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=" + title + API_KEY)
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
        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=" + notBPAward.getNominee() + API_KEY)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.Response").isEqualTo("False")
                .jsonPath("$.Error").isEqualTo("Movie not found!");

        // Movie title is not in table
        webTestClient.get().uri(BEST_PICTURE_WINNER_REQUEST + "?t=xxxxxxxxx" + API_KEY)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.Response").isEqualTo("False")
                .jsonPath("$.Error").isEqualTo("Movie not found!");
    }


    private void rateMovieTestHelper(String title, int rating, String expectedTitle, float expectedRating, int expectedNumRatings){
        webTestClient.post().uri(RATE_MOVIE_REQUEST + "?t=" + title + "&r=" + rating + API_KEY)
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

        webTestClient.post().uri(RATE_MOVIE_REQUEST + "?t=" + "Moulin Rouge" + "&y=" + 1952 + "&r=" + 5 + API_KEY)
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

    // This should be tested properly but I am out of time
    @Test
    void top10Test() throws SQLException {

        // Ordered by rating desc
        Movie movie1 = new Movie(12, "The Lord of the Rings: The Return of the King", 2003, 1400, 9.7f);
        Movie movie2 = new Movie(11, "The Lord of the Rings: The Fellowship of the Ring", 2001, 1500, 9.5f);
        Movie movie3 = new Movie(4, "The Godfather", 1972, 900, 9.1f);
        Movie movie4 = new Movie(13, "The Lord of the Rings: The Two Towers", 2002, 1300, 9.2f);
        Movie movie5 = new Movie(8, "Forrest Gump", 1994, 1100, 9.0f);
        Movie movie6 = new Movie(5, "The Dark Knight", 2008, 1200, 8.9f);
        Movie movie7 = new Movie(6, "Schindlers List", 1993, 800, 8.6f);
        Movie movie8 = new Movie(9, "Inception", 2010, 1000, 8.7f);
        Movie movie9 = new Movie(14, "The Silence of the Lambs", 1991, 850, 8.7f);
        Movie movie10 = new Movie(2, "The Shawshank Redemption", 1994, 500, 8.2f);
        Movie movie11 = new Movie(10, "The Matrix", 1999, 950, 8.4f);
        Movie movie12 = new Movie(15, "Goodfellas", 1990, 750, 7.9f);
        Movie movie13 = new Movie(7, "Fight Club", 1999, 600, 7.8f);
        Movie movie14 = new Movie(3, "Pulp Fiction", 1994, 700, 7.5f);
        Movie movie15 = new Movie(16, "Se7en", 1995, 720, 7.4f);

        List<Movie> movies = new ArrayList<>();

        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);
        movies.add(movie4);
        movies.add(movie5);
        movies.add(movie6);
        movies.add(movie7);
        movies.add(movie8);
        movies.add(movie9);
        movies.add(movie10);
        movies.add(movie11);
        movies.add(movie12);
        movies.add(movie13);
        movies.add(movie14);
        movies.add(movie15);


        testRepo.insertMovies(movies);

        webTestClient.get().uri(TOP_TEN_REQUEST + "?apikey=myToken")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody().json(MoviesApiTestHelper.top10Json);

    }



    static class ContainerConnectionHelper {
        public static void configureDynamicProperties(DynamicPropertyRegistry registry, MySQLContainer<?> dbContainer) {

            registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + dbContainer.getHost() + ":" + dbContainer.getFirstMappedPort() + "/" + dbContainer.getDatabaseName());
            registry.add("spring.r2dbc.username", dbContainer::getUsername);
            registry.add("spring.r2dbc.password", dbContainer::getPassword);
        }

    }
}


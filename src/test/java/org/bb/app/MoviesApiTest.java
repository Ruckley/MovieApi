package org.bb.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestContainerConfig.class)
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


    Award lotrAward = new Award("Best Picture", "The Lord of the Rings: The Return of the King", "Directed by Peter Jackson, based on J.R.R. Tolkiens novel", true, 2003);

    List<Award> testAwards = List.of(
            new Award("Best Picture", "The Godfather", "Epic crime film directed by Francis Ford Coppola", true, 1972),
            new Award("Best Picture", "The Shawshank Redemption", "Based on the novella by Stephen King", false, 1994),
            new Award("Best Picture", "Schindlers List", "Directed by Steven Spielberg, based on the novel Schindlers Ark", true, 1993),
            new Award("Best Picture", "La La Land", "Musical romantic comedy-drama film written and directed by Damien Chazelle", false, 2016),
            lotrAward
    );


    @Autowired
    AwardsRepository awardsRepository;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        MySQLContainerHelper.configureDynamicProperties(registry, mysqlContainer);
    }

    @BeforeAll
    static void setupTable() throws SQLException {
        testRepo.createTable();
    }

    @BeforeEach
    void insertTestData() throws SQLException {
        testRepo.insertAwards(testAwards);
    }


    @Test
    void shouldGetCorrectResult() {

        Mono<Award> result = awardsRepository.findFirstByCategoryAndNomineeContaining("Best Picture", "return of");
        assertTrue(result.block().equals(lotrAward));
    }
}

class MySQLContainerHelper {
    public static void configureDynamicProperties(DynamicPropertyRegistry registry, MySQLContainer<?> dbContainer) {
        dbContainer.start();
        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + dbContainer.getHost() + ":" + dbContainer.getFirstMappedPort() + "/" + dbContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", dbContainer::getUsername);
        registry.add("spring.r2dbc.password", dbContainer::getPassword);
    }

}


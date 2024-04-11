package org.bb.app;

import org.testcontainers.containers.MySQLContainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Create an interface so we can easily test with other DBs if needed in the future
interface MovieTestRepo {
    public void createAwardsTable() throws SQLException;

    public void createMoviesTable() throws SQLException;

    public void dropAllTables() throws SQLException;

    public void insertAwards(List<Award> award) throws SQLException;

    public void insertMovies(List<Movie> movie) throws SQLException;

    public List<Movie> getMovies() throws SQLException;
}

class MovieMySqlTestRepo implements MovieTestRepo {

    private final Connection connection;

    public MovieMySqlTestRepo(MySQLContainer<?> mysqlContainer) throws SQLException {
        this.connection = DriverManager.getConnection(mysqlContainer.getJdbcUrl(), mysqlContainer.getUsername(), mysqlContainer.getPassword());
    }

    public void createAwardsTable() throws SQLException {

        Statement stmt = connection.createStatement();
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS awards (
                id INT AUTO_INCREMENT PRIMARY KEY,
                 category VARCHAR(255),
                  nominee VARCHAR(255),
                  additional_info TEXT,
                  won BOOLEAN,
                  year INT
                );
                """;
        stmt.executeUpdate(createTableSQL);
        stmt.close();
    }

    public void createMoviesTable() throws SQLException {

        Statement stmt = connection.createStatement();
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS movies (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(100) NOT NULL,
                    year INT NOT NULL,
                    num_ratings INT NOT NULL,
                    av_rating FLOAT
                );
                """;
        stmt.executeUpdate(createTableSQL);
        stmt.close();
    }

    public void dropAllTables() throws SQLException {
        Statement stmt = connection.createStatement();
        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS awards");
        stmt.close();

        stmt = connection.createStatement();
        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS movies");
        stmt.close();
    }

    public void insertAwards(List<Award> awards) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO awards (category, nominee, additional_info, won, year) VALUES ");
        for (Award award : awards) {
            Integer won = award.isWon() != null ? (award.isWon() ? 1 : 0) : null;
            sqlBuilder.append(
                    String.format(
                            "('%s', '%s', '%s', %s, %d),",
                            award.getCategory(), award.getNominee(), award.getAdditionalInfo(), won, award.getYear()
                    )
            );
        }
        // Remove the trailing comma
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sqlBuilder.toString());
        stmt.close();
    }

    public void insertMovies(List<Movie> movies) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO movies (title, year, num_ratings, av_rating) VALUES ");
        for (Movie movie : movies) {
            String avRatingValue = movie.getAvRating() != null ? String.valueOf(movie.getAvRating()) : "NULL";
            sqlBuilder.append(
                    String.format(
                            "('%s', '%d', '%d', %s),",
                            movie.getTitle(), movie.getYear(), movie.getNumRatings(), avRatingValue)
            );
        }
        // Remove the trailing comma
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sqlBuilder.toString());
        stmt.close();
    }

    public List<Movie> getMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            Float avRating = resultSet.getFloat("av_rating");
            if (resultSet.wasNull()) {
                avRating = null;
            }
            movies.add(
                    new Movie(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getInt("year"),
                            resultSet.getInt("num_ratings"),
                            avRating
                    )
            );
        }
        resultSet.close();
        statement.close();
        return movies;
    }


}



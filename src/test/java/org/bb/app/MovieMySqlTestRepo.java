package org.bb.app;

import org.testcontainers.containers.MySQLContainer;

import java.sql.*;
import java.util.List;

//Create an interface so we can easily test with other DBs if needed in the future
interface MovieTestRepo {
    public void createTable() throws SQLException;
    public void dropTable() throws SQLException;
    public void insertAwards(List<Award> award) throws SQLException;
}
class MovieMySqlTestRepo implements MovieTestRepo{

    private final Connection connection;
    public MovieMySqlTestRepo(MySQLContainer<?> mysqlContainer) throws SQLException {
        this.connection = DriverManager.getConnection(mysqlContainer.getJdbcUrl(), mysqlContainer.getUsername(), mysqlContainer.getPassword());
    }

    public void createTable() throws SQLException {

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
        connection.createStatement().executeUpdate(createTableSQL);
    }

    public void dropTable() throws SQLException {
        String createTableSQL = "DROP TABLE IF EXISTS awards";
        connection.createStatement().executeUpdate(createTableSQL);
    }

    public void insertAwards(List<Award> awards) throws SQLException{
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO awards (category, nominee, additional_info, won, year) VALUES ");
        for (Award award : awards) {
            sqlBuilder.append(String.format("('%s', '%s', '%s', %s, %d),",
                    award.getCategory(), award.getNominee(), award.getAdditional_info(), award.isWon() ? 1 : 0, award.getYear()));
        }
        // Remove the trailing comma
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sqlBuilder.toString());
    }

}



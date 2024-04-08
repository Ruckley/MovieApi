package org.bb.db;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

interface MovieDbService {
    public Movie movieSearch(String name);
}
@Component
public class MySqlMovieDbService implements MovieDbService{

    private final DbConnectionProvider connectionProvider;

    public MySqlMovieDbService(DbConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        createMovieTableIfNotExists();
    }

    public Movie movieSearch(String name) {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    """
                            CREATE TABLE IF NOT EXISTS movies (
                                year INT,
                                category VARCHAR(255),
                                nominee VARCHAR(255),
                                additional_info TEXT,
                                won BOOLEAN
                            );
                            """
            );
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to Create movies table (if not exists)",e);
        }


            return new Movie(1, true);
    }

//    public void createCustomer(Customer customer) {
//        try (Connection conn = this.connectionProvider.getConnection()) {
//            PreparedStatement pstmt = conn.prepareStatement(
//                    "insert into customers(id,name) values(?,?)"
//            );
//            pstmt.setLong(1, customer.id());
//            pstmt.setString(2, customer.name());
//            pstmt.execute();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public List<Customer> getAllCustomers() {
//        List<Customer> customers = new ArrayList<>();
//
//        try (Connection conn = this.connectionProvider.getConnection()) {
//            PreparedStatement pstmt = conn.prepareStatement(
//                    "select id,name from customers"
//            );
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                long id = rs.getLong("id");
//                String name = rs.getString("name");
//                customers.add(new Customer(id, name));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return customers;
//    }

    private void createMovieTableIfNotExists() {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
            """
                    CREATE TABLE IF NOT EXISTS movies (
                        year INT,
                        category VARCHAR(255),
                        nominee VARCHAR(255),
                        additional_info TEXT,
                        won BOOLEAN
                    );
                    """
            );
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to Create movies table (if not exists)",e);
        }
    }

}

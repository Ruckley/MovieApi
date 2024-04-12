package org.bb.db;

import org.bb.app.model.Award;
import org.bb.app.model.Movie;

import java.sql.SQLException;
import java.util.List;

//Create an interface so we can easily test with other DBs if needed in the future
public interface MovieTestRepo {
    public void createAwardsTable() throws SQLException;

    public void createMoviesTable() throws SQLException;

    public void dropAllTables() throws SQLException;

    public void insertAwards(List<Award> award) throws SQLException;

    public void insertMovies(List<Movie> movie) throws SQLException;

    public List<Movie> getMovies() throws SQLException;
}
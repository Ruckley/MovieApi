package org.bb.db;

public class Movie {
    private String name;
    private int year;
    private boolean winner;

    public Movie(int year, boolean winner) {
        this.year = year;
        this.winner = winner;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public boolean isWinner() {
        return winner;
    }
}

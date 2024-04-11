package org.bb.app;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("movies")
public class Movie {
    @Id
    private int id;

    @Column("title")
    private String title;

    @Column("year")
    private int year;
    @Column("num_ratings")
    private int numRatings;
    @Column("av_rating")
    private Float avRating;

    public Movie() {
    }

    public Movie(String title, int year, int numRatings, Float avRating) {
        this.title = title;
        this.year = year;
        this.numRatings = numRatings;
        this.avRating = avRating;
    }

    public Movie(int id, String title, int year, int numRatings, Float avRating) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.numRatings = numRatings;
        this.avRating = avRating;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public Float getAvRating() {
        return avRating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public void setAvRating(Float avRating) {
        this.avRating = avRating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id && year == movie.year && numRatings == movie.numRatings && Objects.equals(title, movie.title) && Objects.equals(avRating, movie.avRating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, year, numRatings, avRating);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", numRatings=" + numRatings +
                ", avRating=" + avRating +
                '}';
    }
}

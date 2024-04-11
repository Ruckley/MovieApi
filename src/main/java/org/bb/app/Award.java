package org.bb.app;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("awards")
public class Award {
    @Id
    private int id;
    private String category;
    private String nominee;
    @Column("additional_info")
    private String additionalInfo;
    // Use Boolean over boolean to account for Null values in data
    private Boolean won;
    private int year;

    public Award() {}
    public Award(String category, String nominee, String additionalInfo, Boolean won, int year) {
        this.category = category;
        this.nominee = nominee;
        this.additionalInfo = additionalInfo;
        this.won = won;
        this.year = year;
    }

    //For spring


    public String getCategory() {
        return category;
    }

    public String getNominee() {
        return nominee;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public Boolean isWon() {
        return won;
    }

    public int getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Award award = (Award) o;
        return won == award.won && year == award.year && Objects.equals(category, award.category) && Objects.equals(nominee, award.nominee) && Objects.equals(additionalInfo, award.additionalInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, nominee, additionalInfo, won, year);
    }

    @Override
    public String toString() {
        return "Award{" +
                "category='" + category + '\'' +
                ", nominee='" + nominee + '\'' +
                ", additional_info='" + additionalInfo + '\'' +
                ", won=" + won +
                ", year=" + year +
                '}';
    }
}

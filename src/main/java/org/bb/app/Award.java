package org.bb.app;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("awards")
public class Award {
    @Id
    private int id;
    private String category;
    private String nominee;
    private String additional_info;
    private boolean won;
    private int year;

    public Award(String category, String nominee, String additional_info, boolean won, int year) {
        this.category = category;
        this.nominee = nominee;
        this.additional_info = additional_info;
        this.won = won;
        this.year = year;
    }

    //For spring
    public Award() {}

    public String getCategory() {
        return category;
    }

    public String getNominee() {
        return nominee;
    }

    public String getAdditional_info() {
        return additional_info;
    }

    public boolean isWon() {
        return won;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Award{" +
                "category='" + category + '\'' +
                ", nominee='" + nominee + '\'' +
                ", additional_info='" + additional_info + '\'' +
                ", won=" + won +
                ", year=" + year +
                '}';
    }
}

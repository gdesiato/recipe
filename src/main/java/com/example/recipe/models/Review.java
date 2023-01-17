package com.example.recipe.models;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private int rating;

    private String description;

//    @ManyToOne (cascade = CascadeType.ALL, optional = false)
//    private Recipe recipe;

    @Column(name = "recipeId")
    private Long recipeId;

    public void setRating(int rating) {
        if (rating <= 0 || rating > 10) {
            throw new IllegalStateException("Rating must be between 0 and 10.");
        }
        this.rating = rating;
    }
}

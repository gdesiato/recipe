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
public class Step {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private int stepNumber;

    @NotNull
    private String description;
}
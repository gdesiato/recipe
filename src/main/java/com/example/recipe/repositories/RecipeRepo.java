package com.example.recipe.repositories;

import com.example.recipe.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, Long> {

    ArrayList<Recipe> findByNameContaining(String name);



//    ArrayList<Recipe> findByNameContainingAndAverageReviewScoreGreaterThan(String name, Long rating);

}
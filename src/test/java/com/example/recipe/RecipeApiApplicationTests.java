package com.example.recipe;

import com.example.recipe.models.Ingredient;
import com.example.recipe.models.Recipe;
import com.example.recipe.models.Review;
import com.example.recipe.models.Step;
import com.example.recipe.repositories.RecipeRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

@SpringBootTest
@Profile("test")
class RecipeApiApplicationTests implements CommandLineRunner {

	@Autowired
	RecipeRepo recipeRepo;
	@Autowired
	MockMvc mockMvc;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("STARTING WITH TEST DATABASE SETUP");
		if (recipeRepo.findAll().isEmpty()) {

			Ingredient ingredient = Ingredient.builder().name("flour").state("dry").amount("2 cups").build();
			Step step1 = Step.builder().description("put flour in bowl").stepNumber(1).build();
			Step step2 = Step.builder().description("eat it?").stepNumber(2).build();

			Review review = Review.builder().description("tasted pretty bad").rating(2).username("idk").build();

			Recipe recipe1 = Recipe.builder()
					.name("test recipe")
					.difficultyRating(10)
					.minutesToMake(2)
					.ingredients(Set.of(ingredient))
					.steps(Set.of(step1, step2))
					.reviews(Set.of(review))
					.build();
			recipeRepo.save(recipe1);

			ingredient.setId(null);
			Recipe recipe2 = Recipe.builder()
					.steps(Set.of(Step.builder().description("test").build()))
					.ingredients(Set.of(Ingredient.builder().name("test ing").amount("1").state("dry").build()))
					.name("another test recipe")
					.difficultyRating(10)
					.minutesToMake(2)
					.build();
			recipeRepo.save(recipe2);

			Recipe recipe3 = Recipe.builder()
					.steps(Set.of(Step.builder().description("test 2").build()))
					.ingredients(Set.of(Ingredient.builder().name("test ing 2").amount("2").state("wet").build()))
					.name("another another test recipe")
					.difficultyRating(5)
					.minutesToMake(2)
					.build();
			recipeRepo.save(recipe3);

			Recipe recipe4 = Recipe.builder()
					.name("chocolate and potato chips")
					.difficultyRating(10)
					.minutesToMake(1)
					.ingredients(Set.of(
							Ingredient.builder().name("potato chips").amount("1 bag").build(),
							Ingredient.builder().name("chocolate").amount("1 bar").build()))
					.steps(Set.of(
							Step.builder().stepNumber(1).description("eat both items together").build()))
					.reviews(Set.of(
							Review.builder().username("ben").rating(10).description("this stuff is so good").build()
					))
					.build();
			recipeRepo.save(recipe4);

			System.out.println("FINISHED TEST DATABASE SETUP");
		}
	}

	@Test
	public void testGetRecipeByIdSuccessBehavior() throws Exception {
		final long recipeId = 1;

		//set up GET request
		mockMvc.perform(get("/recipes/" + recipeId))

				//print response
				.andDo(print())
				//expect status 200 OK
				.andExpect(status().isOk())
				//expect return Content-Type header as application/json
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				//confirm returned JSON values
				.andExpect(jsonPath("id").value(recipeId))
				.andExpect(jsonPath("minutesToMake").value(2))
				.andExpect(jsonPath("reviews", hasSize(1)))
				.andExpect(jsonPath("ingredients", hasSize(1)))
				.andExpect(jsonPath("steps", hasSize(2)));
	}
}

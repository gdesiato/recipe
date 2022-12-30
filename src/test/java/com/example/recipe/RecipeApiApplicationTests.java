package com.example.recipe;

import com.example.recipe.models.Ingredient;
import com.example.recipe.models.Recipe;
import com.example.recipe.models.Review;
import com.example.recipe.models.Step;
import com.example.recipe.repositories.RecipeRepo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;


@SpringBootTest(classes = RecipeApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeApiApplicationTests implements CommandLineRunner {

	@Autowired
	RecipeRepo recipeRepo;
	MockMvc mockMvc;

	@Test
	@Order(1)
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


	@Test
	@Order(2)
	public void testGetRecipeByIdFailureBehavior() throws Exception {
		final long recipeId = 5000;

		//set up guaranteed to fail in testing environment request
		mockMvc.perform(get("/recipes/" + recipeId))

				//print response
				.andDo(print())
				//expect status 404 NOT FOUND
				.andExpect(status().isNotFound())
				//confirm that HTTP body contains correct error message
				.andExpect(content().string(containsString("No recipe with ID " + recipeId + " could be found.")));
	}


	@Test
	@Order(3)
	public void testGetAllRecipesSuccessBehavior() throws Exception {
		//set up get request for all recipe endpoint
		this.mockMvc.perform(get("/recipes"))

				//expect status is 200 OK
				.andExpect(status().isOk())

				//expect it will be returned as JSON
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				//expect there are 4 entries
				.andExpect(jsonPath("$", hasSize(4)))

				//expect the first entry to have ID 1
				.andExpect(jsonPath("$[0].id").value(1))

				//expect the first entry to have name test recipe
				.andExpect(jsonPath("$[0].name").value("test recipe"))

				//expect the second entry to have id 2
				.andExpect(jsonPath("$[1].id").value(2))

				//expect the second entry to have a minutesToMake value of 2
				.andExpect(jsonPath("$[1].minutesToMake").value(2))

				//expect the third entry to have id 3
				.andExpect(jsonPath("$[2].id").value(3))

				//expect the third entry to have difficulty rating
				.andExpect(jsonPath("$[2].difficultyRating").value(5));
	}


	@Test
	@Order(4)
	public void testCreateNewRecipeSuccessBehavior() throws Exception {
		Ingredient ingredient = Ingredient.builder().name("brown sugar").state("dry").amount("1 cup").build();
		Step step1 = Step.builder().description("heat pan").stepNumber(1).build();
		Step step2 = Step.builder().description("add sugar").stepNumber(2).build();

		Review review = Review.builder().description("was just caramel").rating(3).username("idk").build();

		Recipe recipe = Recipe.builder()
				.name("caramel in a pan")
				.difficultyRating(10)
				.minutesToMake(2)
				.ingredients(Set.of(ingredient))
				.steps(Set.of(step1, step2))
				.reviews(Set.of(review))
				.build();

		MockHttpServletResponse response =
				this.mockMvc.perform(post("/recipes")
						//set request Content-Type header
						.contentType("application/json")
						//set HTTP body equal to JSON based on recipe object
						.content(TestUtil.convertObjectToJsonBytes(recipe))
				)

						//confirm HTTP response meta
						.andExpect(status().isCreated())
						.andExpect(content().contentType("application/json"))
						//confirm Location header with new location of object matches the correct URL structure
						.andExpect(header().string("Location", containsString("http://localhost/recipes/")))

						//confirm some recipe data
						.andExpect(jsonPath("id").isNotEmpty())
						.andExpect(jsonPath("name").value("caramel in a pan"))

						//confirm ingredient data
						.andExpect(jsonPath("ingredients", hasSize(1)))
						.andExpect(jsonPath("ingredients[0].name").value("brown sugar"))
						.andExpect(jsonPath("ingredients[0].amount").value("1 cup"))

						//confirm step data
						.andExpect(jsonPath("steps", hasSize(2)))
						.andExpect(jsonPath("steps[0]").isNotEmpty())
						.andExpect(jsonPath("steps[1]").isNotEmpty())

						//confirm review data
						.andExpect(jsonPath("reviews", hasSize(1)))
						.andExpect(jsonPath("reviews[0].username").value("idk"))
						.andReturn().getResponse();
	}


	@Test
	@Order(5)
	public void testCreateNewRecipeFailureBehavior() throws Exception {

		Recipe recipe = new Recipe();

		//force failure with empty User object
		this.mockMvc.perform(
						post("/recipes")
								//set body equal to empty recipe object
								.content(TestUtil.convertObjectToJsonBytes(recipe))
								//set Content-Type header
								.contentType("application/json")
				)
				//confirm status code 400 BAD REQUEST
				.andExpect(status().isBadRequest())
				//confirm the body only contains a String
				.andExpect(jsonPath("$").isString());
	}


	@Test
//make sure this test runs last
	@Order(11)
	public void testGetAllRecipesFailureBehavior() throws Exception {

		//delete all entries to force error
		recipeRepo.deleteAll();

		//perform GET all recipes
		this.mockMvc.perform(get("/recipes"))

				.andDo(print())

				//expect 404 NOT FOUND
				.andExpect(status().isNotFound())

				//expect error message defined in RecipeService class
				.andExpect(jsonPath("$").value("There are no recipes yet :( feel free to add one though"));
	}

}

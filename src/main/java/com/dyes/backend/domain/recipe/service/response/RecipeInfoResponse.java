package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.*;
import com.dyes.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeInfoResponse {
    private Long recipeId;
    private String recipeName;
    private User user;
    private List<String> recipeDetails;
    private String recipeDescription;
    private String cookingTime;
    private Difficulty difficulty;
    private String recipeMainImage;
    private RecipeMainCategory recipeMainCategory;
    private RecipeSubCategory recipeSubCategory;
    private int servingSize;
    private MainIngredient mainIngredient;
    private String mainIngredientAmount;
    private String ingredientName;
    private String ingredientAmount;
    private String seasoningName;
    private String seasoningAmount;

    public RecipeInfoResponse recipeInfoResponse(
            Recipe recipe, RecipeContent recipeContent, RecipeMainImage recipeMainImage, RecipeCategory recipeCategory,
            RecipeMainIngredient recipeMainIngredient, RecipeSubIngredient recipeSubIngredient, RecipeSeasoningIngredient recipeSeasoningIngredient) {
        return new RecipeInfoResponse(
                recipe.getId(), recipe.getRecipeName(), recipe.getUser(),
                recipeContent.getRecipeDetails(), recipeContent.getRecipeDescription(), recipeContent.getCookingTime(), recipeContent.getDifficulty(),
                recipeMainImage.getRecipeMainImage(), recipeCategory.getRecipeMainCategory(), recipeCategory.getRecipeSubCategory(),
                recipeMainIngredient.getServingSize(), recipeMainIngredient.getMainIngredient(), recipeMainIngredient.getMainIngredientAmount(),
                recipeSubIngredient.getIngredientName(), recipeSubIngredient.getIngredientAmount(),
                recipeSeasoningIngredient.getSeasoningName(), recipeSeasoningIngredient.getSeasoningAmount());
    }
}

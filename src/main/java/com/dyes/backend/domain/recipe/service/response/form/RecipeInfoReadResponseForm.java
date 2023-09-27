package com.dyes.backend.domain.recipe.service.response.form;

import com.dyes.backend.domain.recipe.service.response.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeInfoReadResponseForm {
    private RecipeInfoResponse recipeInfoResponse;
    private RecipeContentResponse recipeContentResponse;
    private RecipeCategoryResponse recipeCategoryResponse;
    private RecipeMainImageResponse recipeMainImageResponse;
    private RecipeMainIngredientResponse recipeMainIngredientResponse;
    private RecipeSubIngredientResponse recipeSubIngredientResponse;
    private RecipeSeasoningIngredientResponse recipeSeasoningIngredientResponse;
}

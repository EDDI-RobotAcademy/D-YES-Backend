package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.RecipeSeasoningIngredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSeasoningIngredientResponse {
    private String seasoningName;
    private String seasoningAmount;

    public RecipeSeasoningIngredientResponse recipeSeasoningIngredientResponse(
            RecipeSeasoningIngredient recipeSeasoningIngredient) {
        return new RecipeSeasoningIngredientResponse(
                recipeSeasoningIngredient.getSeasoningName(), recipeSeasoningIngredient.getSeasoningAmount());
    }
}

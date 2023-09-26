package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.RecipeSubIngredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSubIngredientResponse {
    private String ingredientName;
    private String ingredientAmount;

    public RecipeSubIngredientResponse recipeSubIngredientResponse(RecipeSubIngredient recipeSubIngredient) {
        return new RecipeSubIngredientResponse(recipeSubIngredient.getIngredientName(), recipeSubIngredient.getIngredientAmount());
    }
}

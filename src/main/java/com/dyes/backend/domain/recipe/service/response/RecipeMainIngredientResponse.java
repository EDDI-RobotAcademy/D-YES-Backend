package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.MainIngredient;
import com.dyes.backend.domain.recipe.entity.RecipeMainIngredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMainIngredientResponse {
    private int servingSize;
    private MainIngredient mainIngredient;
    private String mainIngredientAmount;

    public RecipeMainIngredientResponse recipeMainIngredientResponse(RecipeMainIngredient recipeMainIngredient) {
        return new RecipeMainIngredientResponse(recipeMainIngredient.getServingSize(),
                recipeMainIngredient.getMainIngredient(), recipeMainIngredient.getMainIngredientAmount());
    }
}

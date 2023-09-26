package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.Difficulty;
import com.dyes.backend.domain.recipe.entity.RecipeContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContentResponse {
    private List<String> recipeDetails;
    private String recipeDescription;
    private String cookingTime;
    private Difficulty difficulty;

    public RecipeContentResponse recipeContentResponse(RecipeContent recipeContent) {
        return new RecipeContentResponse(recipeContent.getRecipeDetails(), recipeContent.getRecipeDescription(),
                recipeContent.getCookingTime(), recipeContent.getDifficulty());
    }
}

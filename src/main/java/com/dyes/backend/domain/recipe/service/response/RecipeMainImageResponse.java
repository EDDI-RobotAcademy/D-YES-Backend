package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.RecipeMainImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMainImageResponse {
    private String recipeMainImage;

    public RecipeMainImageResponse recipeMainImageResponse(RecipeMainImage recipeMainImage) {
        return new RecipeMainImageResponse(recipeMainImage.getRecipeMainImage());
    }
}

package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.RecipeCategory;
import com.dyes.backend.domain.recipe.entity.RecipeMainCategory;
import com.dyes.backend.domain.recipe.entity.RecipeSubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCategoryResponse {
    private RecipeMainCategory recipeMainCategory;
    private RecipeSubCategory recipeSubCategory;

    public RecipeCategoryResponse recipeCategoryResponse(RecipeCategory recipeCategory) {
        return new RecipeCategoryResponse(recipeCategory.getRecipeMainCategory(), recipeCategory.getRecipeSubCategory());
    }
}

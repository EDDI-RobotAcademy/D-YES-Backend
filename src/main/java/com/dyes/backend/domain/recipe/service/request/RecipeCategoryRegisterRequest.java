package com.dyes.backend.domain.recipe.service.request;

import com.dyes.backend.domain.recipe.entity.RecipeMainCategory;
import com.dyes.backend.domain.recipe.entity.RecipeSubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCategoryRegisterRequest {
    private RecipeMainCategory recipeMainCategory;
    private RecipeSubCategory recipeSubCategory;
}

package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import com.dyes.backend.domain.recipe.entity.RecipeSubIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeSubIngredientRepository extends JpaRepository<RecipeSubIngredient, Long> {
    RecipeSubIngredient findByRecipe(Recipe recipe);
}

package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import com.dyes.backend.domain.recipe.entity.RecipeMainIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeMainIngredientRepository extends JpaRepository<RecipeMainIngredient, Long> {
    RecipeMainIngredient findByRecipe(Recipe recipe);
}

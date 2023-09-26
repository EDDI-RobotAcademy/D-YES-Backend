package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import com.dyes.backend.domain.recipe.entity.RecipeSubIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeSubIngredientRepository extends JpaRepository<RecipeSubIngredient, Long> {
    List<RecipeSubIngredient> findAllByRecipe(Recipe deleteRecipe);
}

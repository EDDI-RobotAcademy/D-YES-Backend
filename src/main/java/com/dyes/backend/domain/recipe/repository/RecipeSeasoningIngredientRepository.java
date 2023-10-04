package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import com.dyes.backend.domain.recipe.entity.RecipeSeasoningIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeSeasoningIngredientRepository extends JpaRepository<RecipeSeasoningIngredient, Long> {

    List<RecipeSeasoningIngredient> findAllByRecipe(Recipe deleteRecipe);
}

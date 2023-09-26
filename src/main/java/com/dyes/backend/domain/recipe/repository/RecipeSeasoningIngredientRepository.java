package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.RecipeSeasoningIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeSeasoningIngredientRepository extends JpaRepository<RecipeSeasoningIngredient, Long> {
}

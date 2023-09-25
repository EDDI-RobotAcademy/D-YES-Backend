package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import com.dyes.backend.domain.recipe.entity.RecipeContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeContentRepository extends JpaRepository<RecipeContent, Long> {
    RecipeContent findByRecipe(Recipe recipe);
}

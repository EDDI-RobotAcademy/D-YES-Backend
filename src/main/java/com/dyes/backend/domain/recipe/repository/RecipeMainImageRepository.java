package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import com.dyes.backend.domain.recipe.entity.RecipeMainImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeMainImageRepository extends JpaRepository<RecipeMainImage, Long> {
    RecipeMainImage findByRecipe(Recipe recipe);
}

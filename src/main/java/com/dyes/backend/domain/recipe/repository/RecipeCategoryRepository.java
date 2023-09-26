package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Long> {
}

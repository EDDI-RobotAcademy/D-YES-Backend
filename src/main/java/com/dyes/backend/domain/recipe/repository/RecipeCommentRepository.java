package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import com.dyes.backend.domain.recipe.entity.RecipeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {
    List<RecipeComment> findAllByRecipe(Recipe recipe);
}

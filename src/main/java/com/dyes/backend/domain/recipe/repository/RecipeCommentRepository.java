package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.RecipeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {
}

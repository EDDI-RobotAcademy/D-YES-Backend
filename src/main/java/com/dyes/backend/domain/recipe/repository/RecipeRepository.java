package com.dyes.backend.domain.recipe.repository;

import com.dyes.backend.domain.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("select r FROM Recipe r join fetch r.user u")
    List<Recipe> findAllWithUser();

    @Query("select r FROM Recipe r join fetch r.user u where r.id = :recipeId")
    Optional<Recipe> findByIdWithUser(Long recipeId);
}

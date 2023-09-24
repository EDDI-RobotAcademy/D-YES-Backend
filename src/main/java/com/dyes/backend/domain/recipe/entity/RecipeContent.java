package com.dyes.backend.domain.recipe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private List<String> recipeDetails;   // 조리법

    private String recipeDescription; // 레시피 설명

    private int cookingTime;        // 조리시간

    @Enumerated(EnumType.STRING)
    private TimeUnit timeUnit;      // 조리시간 단위

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;  // 난이도

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
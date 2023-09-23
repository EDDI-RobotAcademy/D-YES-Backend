package com.dyes.backend.domain.recipe.entity;

import com.dyes.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String recipeDetails;   // 조리법

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String recipeDescripton; // 레시피 설명

    private int cookingTime;        // 조리시간

    @Enumerated(EnumType.STRING)
    private TimeUnit timeUnit;      // 조리시간 단위

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;  // 난이도

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
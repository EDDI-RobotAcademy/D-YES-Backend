package com.dyes.backend.domain.recipe.service.request;

import com.dyes.backend.domain.recipe.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContentRegisterRequest {
    private String recipeDetails;
    private int cookingTime;
    private Difficulty difficulty;
}

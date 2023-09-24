package com.dyes.backend.domain.recipe.service.request;

import com.dyes.backend.domain.recipe.entity.Difficulty;
import com.dyes.backend.domain.recipe.entity.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContentRegisterRequest {
    private List<String> recipeDetails;
    private String recipeDescription;
    private int cookingTime;
    private TimeUnit timeUnit;
    private Difficulty difficulty;
}

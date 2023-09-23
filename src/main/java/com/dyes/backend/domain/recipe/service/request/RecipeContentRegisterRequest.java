package com.dyes.backend.domain.recipe.service.request;

import com.dyes.backend.domain.recipe.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContentRegisterRequest {
    private List<String> recipeDetails;
    private String recipeDiscription;
    private int cookingTime;
    private Difficulty difficulty;
}

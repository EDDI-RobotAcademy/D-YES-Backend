package com.dyes.backend.domain.recipe.service.response.form;

import com.dyes.backend.domain.recipe.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeListResponseForm {
    private Long recipeId;
    private String recipeName;
    private String recipeMainImage;
    private String recipeDescription;
    private int cookingTime;
    private Difficulty difficulty;
    private String nickName;
}

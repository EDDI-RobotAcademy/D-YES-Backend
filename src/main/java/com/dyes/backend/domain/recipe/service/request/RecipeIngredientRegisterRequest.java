package com.dyes.backend.domain.recipe.service.request;

import com.dyes.backend.domain.recipe.entity.MainIngredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientRegisterRequest {
    private MainIngredient mainIngredient;
    private String otherIngredient;
}

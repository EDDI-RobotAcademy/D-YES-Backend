package com.dyes.backend.domain.recipe.service.response.form;

import com.dyes.backend.domain.recipe.service.response.RecipeInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeInfoReadResponseForm {
    private RecipeInfoResponse recipeInfoResponse;
}

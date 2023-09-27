package com.dyes.backend.domain.recipe.service.response;

import com.dyes.backend.domain.recipe.entity.*;
import com.dyes.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeInfoResponse {
    private Long recipeId;
    private String recipeName;
    private User user;

    public RecipeInfoResponse recipeInfoResponse(Recipe recipe) {
        return new RecipeInfoResponse(recipe.getId(), recipe.getRecipeName(), recipe.getUser());
    }
}

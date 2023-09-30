package com.dyes.backend.domain.recipe.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCommentRegisterRequest {
    private Long recipeId;
    private String commentContent;
}

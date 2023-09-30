package com.dyes.backend.domain.recipe.controller.form;

import com.dyes.backend.domain.recipe.service.request.RecipeCommentRegisterRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCommentRegisterRequestForm {
    private String userToken;
    private Long recipeId;
    private String commentContent;
    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public RecipeCommentRegisterRequest toRecipeCommentRegisterRequest() {
        return new RecipeCommentRegisterRequest(recipeId, commentContent);
    }
}

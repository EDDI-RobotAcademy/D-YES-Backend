package com.dyes.backend.domain.recipe.controller.form;

import com.dyes.backend.domain.recipe.service.request.MyRecipeCommentModifyRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyRecipeCommentModifyRequestForm {
    private String userToken;
    private String modifyCommentContent;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public MyRecipeCommentModifyRequest toMyRecipeCommentModifyRequest() {
        return new MyRecipeCommentModifyRequest(modifyCommentContent);
    }
}

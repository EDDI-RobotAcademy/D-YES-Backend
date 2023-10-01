package com.dyes.backend.domain.recipe.service.response.form;

import com.dyes.backend.domain.recipe.service.response.RecipeCommentInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCommentListResponseForm {
    private Long recipeId;
    private List<RecipeCommentInfoResponse> recipeCommentInfoResponseList;
}

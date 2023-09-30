package com.dyes.backend.domain.recipe.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCommentInfoResponse {
    private Long commentId;
    private String nickName;
    private boolean isMyRecipeComment;
    private String content;
    private LocalDate commentDate;
}

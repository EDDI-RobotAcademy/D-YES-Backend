package com.dyes.backend.domain.recipe.service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isMyRecipeComment")
    private boolean isMyRecipeComment;
    private String content;
    private Boolean isDeleted;
    private LocalDate commentDate;
}

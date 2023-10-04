package com.dyes.backend.domain.recipe.service;

import com.dyes.backend.domain.recipe.controller.form.*;
import com.dyes.backend.domain.recipe.service.response.form.RecipeCommentListResponseForm;
import com.dyes.backend.domain.recipe.service.response.form.RecipeInfoReadResponseForm;
import com.dyes.backend.domain.recipe.service.response.form.RecipeListResponseForm;

import java.util.List;

public interface RecipeService {
    boolean registerRecipe(RecipeRegisterForm registerForm);

    List<RecipeListResponseForm> getRecipeList();

    Boolean deleteRecipe(Long recipeId, RecipeDeleteForm deleteForm);

    RecipeInfoReadResponseForm readRecipe(Long recipeId);

    Boolean isMyRecipe(Long recipeId, MyRecipeCheckForm myRecipeCheckForm);

    boolean registerRecipeComment(RecipeCommentRegisterRequestForm registerForm);

    RecipeCommentListResponseForm getRecipeCommentList(Long recipeId, MyRecipeCheckForm myRecipeCheckForm);

    Boolean deleteRecipeComment(Long commentId, MyRecipeCheckForm myRecipeCheckForm);

    Boolean modifyRecipeComment(Long commentId, MyRecipeCommentModifyRequestForm myRecipeCommentModifyRequestForm);
}

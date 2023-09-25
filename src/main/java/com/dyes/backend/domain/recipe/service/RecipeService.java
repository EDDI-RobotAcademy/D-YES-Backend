package com.dyes.backend.domain.recipe.service;

import com.dyes.backend.domain.recipe.controller.form.RecipeRegisterForm;
import com.dyes.backend.domain.recipe.service.response.form.RecipeListResponseForm;

import java.util.List;

public interface RecipeService {
    boolean registerRecipe(RecipeRegisterForm registerForm);

    List<RecipeListResponseForm> getRecipeList();
}

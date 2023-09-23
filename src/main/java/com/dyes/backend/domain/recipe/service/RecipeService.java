package com.dyes.backend.domain.recipe.service;

import com.dyes.backend.domain.recipe.controller.form.RecipeRegisterForm;

public interface RecipeService {
    boolean registerRecipe(RecipeRegisterForm registerForm);
}

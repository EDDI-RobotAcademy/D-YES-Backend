package com.dyes.backend.domain.recipe.controller.form;

import com.dyes.backend.domain.recipe.service.request.RecipeContentRegisterRequest;
import com.dyes.backend.domain.recipe.service.request.RecipeIngredientRegisterRequest;
import com.dyes.backend.domain.recipe.service.request.RecipeMainImageRegisterRequest;
import com.dyes.backend.domain.recipe.service.request.RecipeRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRegisterForm {

    private String userToken;
    private RecipeRegisterRequest recipeRegisterRequest;
    private RecipeContentRegisterRequest recipeContentRegisterRequest;
    private RecipeIngredientRegisterRequest recipeIngredientRegisterRequest;
    private RecipeMainImageRegisterRequest recipeMainImageRegisterRequest;
}

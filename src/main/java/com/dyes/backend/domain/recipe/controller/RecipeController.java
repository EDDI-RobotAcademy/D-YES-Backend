package com.dyes.backend.domain.recipe.controller;

import com.dyes.backend.domain.recipe.controller.form.RecipeRegisterForm;
import com.dyes.backend.domain.recipe.service.RecipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {
    final private RecipeService recipeService;

    @PostMapping("/register")
    public boolean registerRecipe(@RequestBody RecipeRegisterForm registerForm) {
        return recipeService.registerRecipe(registerForm);
    }
}

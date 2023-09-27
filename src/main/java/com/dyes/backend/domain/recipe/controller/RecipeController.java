package com.dyes.backend.domain.recipe.controller;

import com.dyes.backend.domain.recipe.controller.form.MyRecipeCheckForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeDeleteForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeRegisterForm;
import com.dyes.backend.domain.recipe.service.RecipeService;

import com.dyes.backend.domain.recipe.service.response.form.RecipeInfoReadResponseForm;
import com.dyes.backend.domain.recipe.service.response.form.RecipeListResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    public List<RecipeListResponseForm> getRecipeList() {
        return recipeService.getRecipeList();
    }

    @PostMapping("/my_recipe/{recipeId}")
    public boolean isMyRecipe (@PathVariable("recipeId") Long recipeId, @RequestBody MyRecipeCheckForm myRecipeCheckForm) {
        return recipeService.isMyRecipe(recipeId, myRecipeCheckForm);
    }

    @DeleteMapping("/delete/{recipeId}")
    public boolean deleteRecipe (@PathVariable("recipeId") Long recipeId,
                                 @RequestBody RecipeDeleteForm deleteForm) {
        return recipeService.deleteRecipe(recipeId, deleteForm);
    }

    @GetMapping("/read/{recipeId}")
    public RecipeInfoReadResponseForm readRecipe(@PathVariable("recipeId") Long recipeId) {
        return recipeService.readRecipe(recipeId);
    }
}

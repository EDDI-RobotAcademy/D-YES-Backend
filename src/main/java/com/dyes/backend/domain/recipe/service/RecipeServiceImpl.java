package com.dyes.backend.domain.recipe.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.recipe.controller.form.RecipeRegisterForm;
import com.dyes.backend.domain.recipe.entity.*;
import com.dyes.backend.domain.recipe.repository.RecipeContentRepository;
import com.dyes.backend.domain.recipe.repository.RecipeIngredientRepository;
import com.dyes.backend.domain.recipe.repository.RecipeMainImageRepository;
import com.dyes.backend.domain.recipe.repository.RecipeRepository;
import com.dyes.backend.domain.recipe.service.request.RecipeContentRegisterRequest;
import com.dyes.backend.domain.recipe.service.request.RecipeIngredientRegisterRequest;
import com.dyes.backend.domain.recipe.service.request.RecipeMainImageRegisterRequest;
import com.dyes.backend.domain.recipe.service.request.RecipeRegisterRequest;
import com.dyes.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    final private RecipeRepository recipeRepository;
    final private RecipeContentRepository recipeContentRepository;
    final private RecipeIngredientRepository recipeIngredientRepository;
    final private RecipeMainImageRepository recipeMainImageRepository;
    final private AuthenticationService authenticationService;

    @Override
    public boolean registerRecipe(RecipeRegisterForm registerForm) {
        log.info("Registering a new recipe");

        String userToken = registerForm.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        RecipeRegisterRequest recipeRegisterRequest = registerForm.getRecipeRegisterRequest();
        RecipeContentRegisterRequest recipeContentRegisterRequest = registerForm.getRecipeContentRegisterRequest();
        RecipeIngredientRegisterRequest recipeIngredientRegisterRequest = registerForm.getRecipeIngredientRegisterRequest();
        RecipeMainImageRegisterRequest recipeMainImageRegisterRequest = registerForm.getRecipeMainImageRegisterRequest();

        if(user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        try {
            Recipe recipe = Recipe.builder()
                    .recipeName(recipeRegisterRequest.getRecipeName())
                    .user(user)
                    .build();

            recipeRepository.save(recipe);

            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .mainIngredient(recipeIngredientRegisterRequest.getMainIngredient())
                    .otherIngredientList(recipeIngredientRegisterRequest.getOtherIngredientList())
                    .build();

            recipeIngredientRepository.save(recipeIngredient);

            RecipeContent recipeContent = RecipeContent.builder()
                    .recipeDetails(recipeContentRegisterRequest.getRecipeDetails())
                    .cookingTime(recipeContentRegisterRequest.getCookingTime())
                    .difficulty(recipeContentRegisterRequest.getDifficulty())
                    .build();

            recipeContentRepository.save(recipeContent);

            RecipeMainImage recipeMainImage = RecipeMainImage.builder()
                    .recipeMainImage(recipeMainImageRegisterRequest.getRecipeMainImage())
                    .build();

            recipeMainImageRepository.save(recipeMainImage);

            log.info("Recipe registration successful");
            return true;

        } catch (Exception e) {
            log.error("Failed to register the recipe: {}", e.getMessage(), e);
            return false;
        }
    }

}

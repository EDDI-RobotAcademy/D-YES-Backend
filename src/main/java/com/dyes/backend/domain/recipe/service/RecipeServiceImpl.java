package com.dyes.backend.domain.recipe.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.recipe.controller.form.RecipeDeleteForm;
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
import com.dyes.backend.domain.recipe.service.response.form.RecipeListResponseForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    final private RecipeRepository recipeRepository;
    final private RecipeContentRepository recipeContentRepository;
    final private RecipeIngredientRepository recipeIngredientRepository;
    final private RecipeMainImageRepository recipeMainImageRepository;
    final private AuthenticationService authenticationService;
    final private UserProfileRepository userProfileRepository;

    // 레시피 등록
    @Override
    public boolean registerRecipe(RecipeRegisterForm registerForm) {
        log.info("Registering a new recipe");

        String userToken = registerForm.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        if(user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        RecipeRegisterRequest recipeRegisterRequest = registerForm.getRecipeRegisterRequest();
        RecipeContentRegisterRequest recipeContentRegisterRequest = registerForm.getRecipeContentRegisterRequest();
        RecipeIngredientRegisterRequest recipeIngredientRegisterRequest = registerForm.getRecipeIngredientRegisterRequest();
        RecipeMainImageRegisterRequest recipeMainImageRegisterRequest = registerForm.getRecipeMainImageRegisterRequest();

        try {
            Recipe recipe = Recipe.builder()
                    .recipeName(recipeRegisterRequest.getRecipeName())
                    .user(user)
                    .build();

            recipeRepository.save(recipe);

            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .mainIngredient(recipeIngredientRegisterRequest.getMainIngredient())
                    .otherIngredientList(recipeIngredientRegisterRequest.getOtherIngredient())
                    .recipe(recipe)
                    .build();

            recipeIngredientRepository.save(recipeIngredient);

            RecipeContent recipeContent = RecipeContent.builder()
                    .recipeDetails(recipeContentRegisterRequest.getRecipeDetails())
                    .recipeDescription(recipeContentRegisterRequest.getRecipeDescription())
                    .cookingTime(recipeContentRegisterRequest.getCookingTime())
                    .timeUnit(recipeContentRegisterRequest.getTimeUnit())
                    .difficulty(recipeContentRegisterRequest.getDifficulty())
                    .recipe(recipe)
                    .build();

            recipeContentRepository.save(recipeContent);

            RecipeMainImage recipeMainImage = RecipeMainImage.builder()
                    .recipeMainImage(recipeMainImageRegisterRequest.getRecipeMainImage())
                    .recipe(recipe)
                    .build();

            recipeMainImageRepository.save(recipeMainImage);

            log.info("Recipe registration successful");
            return true;

        } catch (Exception e) {
            log.error("Failed to register the recipe: {}", e.getMessage(), e);
            return false;
        }
    }

    // 레시피 목록
    @Override
    public List<RecipeListResponseForm> getRecipeList() {
        log.info("Reading recipe list");

        List<RecipeListResponseForm> recipeListResponseListForm = new ArrayList<>();

        try {
            List<Recipe> recipeList = recipeRepository.findAllWithUser();
            for (Recipe recipe : recipeList) {
                RecipeMainImage recipeMainImage = recipeMainImageRepository.findByRecipe(recipe);
                RecipeContent recipeContent = recipeContentRepository.findByRecipe(recipe);
                User user = recipe.getUser();
                Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
                String nickName = null;
                if(maybeUserProfile.isPresent()) {
                    UserProfile userProfile = maybeUserProfile.get();
                    nickName = userProfile.getNickName();
                }
                RecipeListResponseForm recipeListResponseForm
                        = new RecipeListResponseForm(
                        recipe.getId(),
                        recipe.getRecipeName(),
                        recipeMainImage.getRecipeMainImage(),
                        recipeContent.getRecipeDescription(),
                        recipeContent.getCookingTime(),
                        recipeContent.getDifficulty(),
                        nickName);
                recipeListResponseListForm.add(recipeListResponseForm);

                log.info("Recipe list read successful");
            }

            return recipeListResponseListForm;
        } catch (Exception e) {
            log.error("Failed to read the recipe list: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Boolean deleteRecipe(Long recipeId, RecipeDeleteForm deleteForm) {
        log.info("Deleting recipe with ID: {}", recipeId);

        String userToken = deleteForm.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        if(user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        Optional<Recipe> maybeRecipe = recipeRepository.findById(recipeId);
        if (maybeRecipe.isEmpty()) {
            log.info("Recipe is empty");
            return false;
        }

        Recipe deleteRecipe = maybeRecipe.get();
        if (!deleteRecipe.getUser().getId().equals(user.getId())) {
            log.info("UserId do not match");
            return false;
        }

        try {
            RecipeMainImage recipeMainImage = recipeMainImageRepository.findByRecipe(deleteRecipe);
            RecipeContent recipeContent = recipeContentRepository.findByRecipe(deleteRecipe);
            RecipeIngredient recipeIngredient = recipeIngredientRepository.findByRecipe(deleteRecipe);

            recipeMainImageRepository.delete(recipeMainImage);
            recipeContentRepository.delete(recipeContent);
            recipeIngredientRepository.delete(recipeIngredient);
            recipeRepository.delete(deleteRecipe);

            log.info("Recipe deletion successful for farm with ID: {}", recipeId);
            return true;

        } catch (Exception e) {
            log.error("Failed to delete the recipe: {}", e.getMessage(), e);
            return false;
        }
    }

}

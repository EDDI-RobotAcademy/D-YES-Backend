package com.dyes.backend.domain.recipe.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.recipe.controller.form.MyRecipeCheckForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeDeleteForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeIngredientInfoForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeRegisterForm;
import com.dyes.backend.domain.recipe.entity.*;
import com.dyes.backend.domain.recipe.repository.*;
import com.dyes.backend.domain.recipe.service.request.*;
import com.dyes.backend.domain.recipe.service.response.*;
import com.dyes.backend.domain.recipe.service.response.form.RecipeListResponseForm;
import com.dyes.backend.domain.recipe.service.response.form.RecipeInfoReadResponseForm;
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
    final private RecipeMainIngredientRepository recipeMainIngredientRepository;
    final private RecipeSubIngredientRepository recipeSubIngredientRepository;
    final private RecipeSeasoningIngredientRepository recipeSeasoningIngredientRepository;
    final private RecipeCategoryRepository recipeCategoryRepository;
    final private RecipeMainImageRepository recipeMainImageRepository;
    final private AuthenticationService authenticationService;
    final private UserProfileRepository userProfileRepository;

    // 레시피 등록
    @Override
    public boolean registerRecipe(RecipeRegisterForm registerForm) {
        log.info("Registering a new recipe");

        String userToken = registerForm.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        if (user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        RecipeRegisterRequest recipeRegisterRequest = registerForm.getRecipeRegisterRequest();
        RecipeContentRegisterRequest recipeContentRegisterRequest = registerForm.getRecipeContentRegisterRequest();
        RecipeCategoryRegisterRequest recipeCategoryRegisterRequest = registerForm.getRecipeCategoryRegisterRequest();
        RecipeIngredientRegisterRequest recipeIngredientRegisterRequest = registerForm.getRecipeIngredientRegisterRequest();
        RecipeMainImageRegisterRequest recipeMainImageRegisterRequest = registerForm.getRecipeMainImageRegisterRequest();

        try {
            Recipe recipe = Recipe.builder()
                    .recipeName(recipeRegisterRequest.getRecipeName())
                    .user(user)
                    .build();

            recipeRepository.save(recipe);

            RecipeMainIngredient recipeMainIngredient = RecipeMainIngredient.builder()
                    .servingSize(recipeIngredientRegisterRequest.getServingSize())
                    .mainIngredient(recipeIngredientRegisterRequest.getMainIngredient())
                    .mainIngredientAmount(recipeIngredientRegisterRequest.getMainIngredientAmount())
                    .recipe(recipe)
                    .build();
            recipeMainIngredientRepository.save(recipeMainIngredient);

            List<RecipeIngredientInfoForm> otherIngredienList = recipeIngredientRegisterRequest.getOtherIngredientList();
            for (RecipeIngredientInfoForm recipeIngredientInfoForm : otherIngredienList) {
                RecipeSubIngredient recipeSubIngredient = RecipeSubIngredient.builder()
                        .ingredientName(recipeIngredientInfoForm.getIngredientName())
                        .ingredientAmount(recipeIngredientInfoForm.getIngredientAmount())
                        .recipe(recipe)
                        .build();
                recipeSubIngredientRepository.save(recipeSubIngredient);
            }

            List<RecipeIngredientInfoForm> seasoningList = recipeIngredientRegisterRequest.getSeasoningList();
            for (RecipeIngredientInfoForm recipeIngredientInfoForm : seasoningList) {
                RecipeSeasoningIngredient recipeSeasoningIngredient = RecipeSeasoningIngredient.builder()
                        .seasoningName(recipeIngredientInfoForm.getIngredientAmount())
                        .seasoningAmount(recipeIngredientInfoForm.getIngredientAmount())
                        .recipe(recipe)
                        .build();
                recipeSeasoningIngredientRepository.save(recipeSeasoningIngredient);
            }

            RecipeContent recipeContent = RecipeContent.builder()
                    .recipeDetails(recipeContentRegisterRequest.getRecipeDetails())
                    .recipeDescription(recipeContentRegisterRequest.getRecipeDescription())
                    .cookingTime(recipeContentRegisterRequest.getCookingTime())
                    .difficulty(recipeContentRegisterRequest.getDifficulty())
                    .recipe(recipe)
                    .build();

            recipeContentRepository.save(recipeContent);

            RecipeCategory recipeCategory = RecipeCategory.builder()
                    .recipeMainCategory(recipeCategoryRegisterRequest.getRecipeMainCategory())
                    .recipeSubCategory(recipeCategoryRegisterRequest.getRecipeSubCategory())
                    .recipe(recipe)
                    .build();

            recipeCategoryRepository.save(recipeCategory);

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
                if (maybeUserProfile.isPresent()) {
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

        if (user == null) {
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
            RecipeMainIngredient recipeMainIngredient = recipeMainIngredientRepository.findByRecipe(deleteRecipe);
            RecipeCategory recipeCategory = recipeCategoryRepository.findByRecipe(deleteRecipe);
            List<RecipeSubIngredient> recipeSubIngredientList = recipeSubIngredientRepository.findAllByRecipe(deleteRecipe);
            List<RecipeSeasoningIngredient> recipeSeasoningIngredientList = recipeSeasoningIngredientRepository.findAllByRecipe(deleteRecipe);

            recipeMainImageRepository.delete(recipeMainImage);
            recipeContentRepository.delete(recipeContent);
            recipeMainIngredientRepository.delete(recipeMainIngredient);
            recipeCategoryRepository.delete(recipeCategory);
            for (RecipeSubIngredient recipeSubIngredient : recipeSubIngredientList) {
                recipeSubIngredientRepository.delete(recipeSubIngredient);
            }
            for (RecipeSeasoningIngredient recipeSeasoningIngredient : recipeSeasoningIngredientList) {
                recipeSeasoningIngredientRepository.delete(recipeSeasoningIngredient);
            }
            recipeRepository.delete(deleteRecipe);
            log.info("Recipe deletion successful for recipe with ID: {}", recipeId);
            return true;

        } catch (Exception e) {
            log.error("Failed to delete the recipe: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RecipeInfoReadResponseForm readRecipe(Long recipeId) {
        log.info("Reading recipe with ID: {}", recipeId);

        Optional<Recipe> maybeRecipe = recipeRepository.findById(recipeId);
        if (maybeRecipe.isEmpty()) {
            log.info("Recipe is empty");
            return null;
        }

        try {
            Recipe recipe = maybeRecipe.get();
            RecipeContent recipeContent = recipeContentRepository.findByRecipe(recipe);
            RecipeCategory recipeCategory = recipeCategoryRepository.findByRecipe(recipe);
            RecipeMainImage recipeMainImage = recipeMainImageRepository.findByRecipe(recipe);
            RecipeMainIngredient recipeMainIngredient = recipeMainIngredientRepository.findByRecipe(recipe);
            RecipeSubIngredient recipeSubIngredient = recipeSubIngredientRepository.findByRecipe(recipe);
            RecipeSeasoningIngredient recipeSeasoningIngredient = recipeSeasoningIngredientRepository.findByRecipe(recipe);

            RecipeInfoResponse recipeInfoResponse = new RecipeInfoResponse().recipeInfoResponse(recipe);
            RecipeContentResponse recipeContentResponse = new RecipeContentResponse().recipeContentResponse(recipeContent);
            RecipeCategoryResponse recipeCategoryResponse = new RecipeCategoryResponse().recipeCategoryResponse(recipeCategory);
            RecipeMainImageResponse recipeMainImageResponse = new RecipeMainImageResponse().recipeMainImageResponse(recipeMainImage);
            RecipeMainIngredientResponse recipeMainIngredientResponse =
                    new RecipeMainIngredientResponse().recipeMainIngredientResponse(recipeMainIngredient);
            RecipeSubIngredientResponse recipeSubIngredientResponse =
                    new RecipeSubIngredientResponse().recipeSubIngredientResponse(recipeSubIngredient);
            RecipeSeasoningIngredientResponse recipeSeasoningIngredientResponse =
                    new RecipeSeasoningIngredientResponse().recipeSeasoningIngredientResponse(recipeSeasoningIngredient);


            RecipeInfoReadResponseForm recipeInfoReadResponseForm = new RecipeInfoReadResponseForm(
                    recipeInfoResponse, recipeContentResponse, recipeCategoryResponse, recipeMainImageResponse,
                    recipeMainIngredientResponse, recipeSubIngredientResponse, recipeSeasoningIngredientResponse);

            log.info("Recipe read successful for recipe with ID: {}", recipeId);
            return recipeInfoReadResponseForm;

        } catch (Exception e) {
            log.error("Failed to read the recipe: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Boolean isMyRecipe(Long recipeId, MyRecipeCheckForm myRecipeCheckForm) {
        log.info("Reading my recipe with ID: {}", recipeId);

        String userToken = myRecipeCheckForm.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        if (user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        Optional<Recipe> maybeRecipe = recipeRepository.findById(recipeId);
        if (maybeRecipe.isEmpty()) {
            log.info("Recipe is empty");
            return false;
        }

        Recipe isMyRecipe = maybeRecipe.get();
        if (!isMyRecipe.getUser().getId().equals(user.getId())) {
            log.info("UserId do not match");
            return false;
        }

       return true;
    }

}

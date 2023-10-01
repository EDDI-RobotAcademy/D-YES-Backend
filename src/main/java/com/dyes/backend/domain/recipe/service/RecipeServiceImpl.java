package com.dyes.backend.domain.recipe.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.recipe.controller.form.*;
import com.dyes.backend.domain.recipe.entity.*;
import com.dyes.backend.domain.recipe.repository.*;
import com.dyes.backend.domain.recipe.service.request.*;
import com.dyes.backend.domain.recipe.service.response.RecipeCommentInfoResponse;
import com.dyes.backend.domain.recipe.service.response.form.RecipeCommentListResponseForm;
import com.dyes.backend.domain.recipe.service.response.form.RecipeListResponseForm;
import com.dyes.backend.domain.recipe.service.response.form.RecipeInfoReadResponseForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    final private RecipeCommentRepository recipeCommentRepository;
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

    // 레시피 목록 조회
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

    // 레시피 삭제
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

    // 레시피 상세 읽기
    @Override
    public RecipeInfoReadResponseForm readRecipe(Long recipeId) {
        log.info("Reading recipe with ID: {}", recipeId);

        Optional<Recipe> maybeRecipe = recipeRepository.findByIdWithUser(recipeId);
        if (maybeRecipe.isEmpty()) {
            log.info("Recipe is empty");
            return null;
        }

        try {
            Recipe recipe = maybeRecipe.get();
            User user = recipe.getUser();
            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);

            String nickName = null;
            if (maybeUserProfile.isPresent()) {
                UserProfile userProfile = maybeUserProfile.get();
                nickName = userProfile.getNickName();
            }

            RecipeContent recipeContent = recipeContentRepository.findByRecipe(recipe);
            RecipeCategory recipeCategory = recipeCategoryRepository.findByRecipe(recipe);
            RecipeMainImage recipeMainImage = recipeMainImageRepository.findByRecipe(recipe);
            RecipeMainIngredient recipeMainIngredient = recipeMainIngredientRepository.findByRecipe(recipe);
            List<RecipeSubIngredient> recipeSubIngredientList = recipeSubIngredientRepository.findAllByRecipe(recipe);
            List<RecipeSeasoningIngredient> recipeSeasoningIngredientList = recipeSeasoningIngredientRepository.findAllByRecipe(recipe);

            // 레시피 이름
            RecipeRegisterRequest recipeRegisterRequest
                    = new RecipeRegisterRequest(recipe.getRecipeName());

            // 레시피 내용
            RecipeContentRegisterRequest recipeContentRegisterRequest
                    = new RecipeContentRegisterRequest(
                    recipeContent.getRecipeDetails(),
                    recipeContent.getRecipeDescription(),
                    recipeContent.getCookingTime(),
                    recipeContent.getDifficulty());

            // 레시피 카테고리
            RecipeCategoryRegisterRequest recipeCategoryRegisterRequest
                    = new RecipeCategoryRegisterRequest(
                    recipeCategory.getRecipeMainCategory(),
                    recipeCategory.getRecipeSubCategory());

            // 레시피 부재료
            List<RecipeIngredientInfoForm> otherIngredientList = new ArrayList<>();
            for (RecipeSubIngredient recipeSubIngredient : recipeSubIngredientList) {
                RecipeIngredientInfoForm subRecipeIngredientInfoForm
                        = new RecipeIngredientInfoForm(
                        recipeSubIngredient.getIngredientName(),
                        recipeSubIngredient.getIngredientAmount());

                otherIngredientList.add(subRecipeIngredientInfoForm);
            }

            // 레시피 기타 재료
            List<RecipeIngredientInfoForm> seasoningList = new ArrayList<>();
            for (RecipeSeasoningIngredient recipeSeasoningIngredient : recipeSeasoningIngredientList) {
                RecipeIngredientInfoForm subRecipeIngredientInfoForm
                        = new RecipeIngredientInfoForm(
                        recipeSeasoningIngredient.getSeasoningName(),
                        recipeSeasoningIngredient.getSeasoningAmount());

                seasoningList.add(subRecipeIngredientInfoForm);
            }

            // 레시피 전체 재료
            RecipeIngredientRegisterRequest recipeIngredientRegisterRequest
                    = new RecipeIngredientRegisterRequest(
                    recipeMainIngredient.getServingSize(),
                    recipeMainIngredient.getMainIngredient(),
                    recipeMainIngredient.getMainIngredientAmount(),
                    otherIngredientList,
                    seasoningList);

            // 레시피 이미지
            RecipeMainImageRegisterRequest recipeMainImageRegisterRequest
                    = new RecipeMainImageRegisterRequest(recipeMainImage.getRecipeMainImage());

            // 전체 정보 담기
            RecipeInfoReadResponseForm recipeInfoReadResponseForm
                    = new RecipeInfoReadResponseForm(
                    nickName,
                    recipeRegisterRequest,
                    recipeContentRegisterRequest,
                    recipeCategoryRegisterRequest,
                    recipeIngredientRegisterRequest,
                    recipeMainImageRegisterRequest);

            log.info("Recipe read successful for recipe with ID: {}", recipeId);
            return recipeInfoReadResponseForm;

        } catch (Exception e) {
            log.error("Failed to read the recipe: {}", e.getMessage(), e);
            return null;
        }
    }

    // 사용자의 레시피인지 확인
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

    // 레시피에 댓글 달기
    @Override
    public boolean registerRecipeComment(RecipeCommentRegisterRequestForm registerForm) {
        log.info("Registering a new recipe comment");

        UserAuthenticationRequest userAuthenticationRequest = registerForm.toUserAuthenticationRequest();
        String userToken = userAuthenticationRequest.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        if (user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        RecipeCommentRegisterRequest recipeCommentRegisterRequest = registerForm.toRecipeCommentRegisterRequest();
        Long recipeId = recipeCommentRegisterRequest.getRecipeId();
        String comment = recipeCommentRegisterRequest.getCommentContent();

        try {
            Optional<Recipe> maybeRecipe = recipeRepository.findById(recipeId);
            if (maybeRecipe.isEmpty()) {
                return false;
            }
            Recipe recipe = maybeRecipe.get();
            RecipeComment recipeComment = RecipeComment.builder()
                    .commentContent(comment)
                    .user(user)
                    .recipe(recipe)
                    .build();
            recipeCommentRepository.save((recipeComment));
            log.info("Recipe comment register successful");
            return true;
        } catch (Exception e) {
            log.error("Failed to register the recipe comment: {}", e.getMessage(), e);
            return false;
        }
    }

    // 레시피 댓글 목록 조회
    @Override
    public RecipeCommentListResponseForm getRecipeCommentList(Long recipeId, MyRecipeCheckForm myRecipeCheckForm) {
        log.info("Reading comments with recipe id: {}", recipeId);

        Optional<Recipe> maybeRecipe = recipeRepository.findById(recipeId);
        if (maybeRecipe.isEmpty()) {
            log.info("Unable to find recipe with recipe id: {}", recipeId);
            return null;
        } else if (maybeRecipe.isPresent()) {
            Recipe recipe = maybeRecipe.get();

            // 해당 레시피에 연결된 Recipe Comment를 모두 가져오기
            List<RecipeComment> recipeCommentList = recipeCommentRepository.findAllByRecipe(recipe);
            List<RecipeCommentInfoResponse> recipeCommentInfoResponseList = new ArrayList<>();

            String userToken = myRecipeCheckForm.getUserToken();
            User user = authenticationService.findUserByUserToken(userToken);

            for (RecipeComment recipeComment : recipeCommentList) {
                Long commentId;
                String nickName = "";
                String commentContent;
                LocalDate commentDate;
                Boolean isDeleted;
                boolean isMyRecipeComment = false;
                commentId = recipeComment.getCommentId();
                commentContent = recipeComment.getCommentContent();
                commentDate = recipeComment.getRegisteredDate();
                isDeleted = recipeComment.getIsDeleted();

                User userByRecipeComment = recipeComment.getUser();
                Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(userByRecipeComment);
                if (maybeUserProfile.isPresent()) {
                    UserProfile userProfile = maybeUserProfile.get();
                    nickName = userProfile.getNickName();
                }
                if (user == null) {
                    log.info("Unable to find user with user token: {}", userToken);
                    isMyRecipeComment = false;

                } else if (user != null) {
                    if (user.getId().equals(userByRecipeComment.getId())) {
                        isMyRecipeComment = true;
                    }
                }
                RecipeCommentInfoResponse recipeCommentInfoResponse
                        = new RecipeCommentInfoResponse(
                        commentId,
                        nickName,
                        isMyRecipeComment,
                        commentContent,
                        isDeleted,
                        commentDate);
                recipeCommentInfoResponseList.add(recipeCommentInfoResponse);
            }

            RecipeCommentListResponseForm responseForm
                    = new RecipeCommentListResponseForm(recipeId, recipeCommentInfoResponseList);
            return responseForm;
        }
        return null;
    }

    // 나의 레시피 댓글 삭제
    @Override
    public Boolean deleteRecipeComment(Long commentId, MyRecipeCheckForm myRecipeCheckForm) {
        String userToken = myRecipeCheckForm.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        if (user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        Optional<RecipeComment> maybeRecipeComment = recipeCommentRepository.findById(commentId);
        if (maybeRecipeComment.isEmpty()) {
            log.info("Unable to find RecipeComment with commentId: {}", commentId);
            return false;
        }

        RecipeComment recipeComment = maybeRecipeComment.get();
        if (!recipeComment.getUser().getId().equals(user.getId())) {
            return false;
        }
        recipeComment.setIsDeleted(true);
        recipeCommentRepository.save(recipeComment);

        return true;
    }

    @Override
    public Boolean modifyRecipeComment(Long commentId, MyRecipeCommentModifyRequestForm myRecipeCommentModifyRequestForm) {
        UserAuthenticationRequest userAuthenticationRequest = myRecipeCommentModifyRequestForm.toUserAuthenticationRequest();
        String userToken = userAuthenticationRequest.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        if (user == null) {
            log.info("Unable to find user with user token: {}", userToken);
            return false;
        }

        Optional<RecipeComment> maybeRecipeComment = recipeCommentRepository.findById(commentId);
        if (maybeRecipeComment.isEmpty()) {
            log.info("Unable to find RecipeComment with commentId: {}", commentId);
            return false;
        }

        RecipeComment recipeComment = maybeRecipeComment.get();
        if (!recipeComment.getUser().getId().equals(user.getId())) {
            log.info("Unable to modify RecipeComment with commentId: {} as the user is different.", commentId);
            return false;
        }

        if(recipeComment.getIsDeleted() != null && recipeComment.getIsDeleted().equals(true)) {
            log.info("Unable to modify RecipeComment with commentId: {} as the comment has been deleted.", commentId);
            return false;
        }

        MyRecipeCommentModifyRequest myRecipeCommentModifyRequest = myRecipeCommentModifyRequestForm.toMyRecipeCommentModifyRequest();
        String modifyCommentContent = myRecipeCommentModifyRequest.getModifyCommentContent();
        recipeComment.setCommentContent(modifyCommentContent);
        recipeCommentRepository.save(recipeComment);
        return true;
    }
}

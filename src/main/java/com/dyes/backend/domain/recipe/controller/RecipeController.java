package com.dyes.backend.domain.recipe.controller;

import com.dyes.backend.domain.recipe.controller.form.MyRecipeCheckForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeCommentRegisterRequestForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeDeleteForm;
import com.dyes.backend.domain.recipe.controller.form.RecipeRegisterForm;
import com.dyes.backend.domain.recipe.service.RecipeService;

import com.dyes.backend.domain.recipe.service.response.form.RecipeCommentListResponseForm;
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

    // 레시피 등록
    @PostMapping("/register")
    public boolean registerRecipe(@RequestBody RecipeRegisterForm registerForm) {
        return recipeService.registerRecipe(registerForm);
    }

    // 레시피 목록 조회
    @GetMapping("/list")
    public List<RecipeListResponseForm> getRecipeList() {
        return recipeService.getRecipeList();
    }

    // 사용자의 레시피인지 확인
    @PostMapping("/my_recipe/{recipeId}")
    public boolean isMyRecipe (@PathVariable("recipeId") Long recipeId, @RequestBody MyRecipeCheckForm myRecipeCheckForm) {
        return recipeService.isMyRecipe(recipeId, myRecipeCheckForm);
    }

    // 레시피 삭제
    @DeleteMapping("/delete/{recipeId}")
    public boolean deleteRecipe (@PathVariable("recipeId") Long recipeId,
                                 @RequestBody RecipeDeleteForm deleteForm) {
        return recipeService.deleteRecipe(recipeId, deleteForm);
    }

    // 레시피 상세 읽기
    @GetMapping("/read/{recipeId}")
    public RecipeInfoReadResponseForm readRecipe(@PathVariable("recipeId") Long recipeId) {
        return recipeService.readRecipe(recipeId);
    }

    // 레시피에 댓글 달기
    @PostMapping("/comment/register")
    public boolean registerRecipeComment(@RequestBody RecipeCommentRegisterRequestForm registerForm) {
        return recipeService.registerRecipeComment(registerForm);
    }

    // 레시피 댓글 목록 조회
    @PostMapping("/comment/list/{recipeId}")
    public RecipeCommentListResponseForm getRecipeCommentList(@PathVariable("recipeId") Long recipeId,
                                                              @RequestBody MyRecipeCheckForm myRecipeCheckForm) {
        return recipeService.getRecipeCommentList(recipeId, myRecipeCheckForm);
    }

    // 나의 레시피 댓글 삭제
    @DeleteMapping("/comment/delete/{commentId}")
    public Boolean deleteRecipeComment(@PathVariable("commentId") Long commentId,
                                       @RequestBody MyRecipeCheckForm myRecipeCheckForm) {
        return recipeService.deleteRecipeComment(commentId, myRecipeCheckForm);
    }
}

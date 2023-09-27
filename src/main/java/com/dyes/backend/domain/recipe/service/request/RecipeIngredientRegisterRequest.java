package com.dyes.backend.domain.recipe.service.request;

import com.dyes.backend.domain.recipe.controller.form.RecipeIngredientInfoForm;
import com.dyes.backend.domain.recipe.entity.MainIngredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientRegisterRequest {
    private int servingSize;
    private MainIngredient mainIngredient;
    private String mainIngredientAmount;
    private List<RecipeIngredientInfoForm> otherIngredienList = new ArrayList<>();
    private List<RecipeIngredientInfoForm> seasoningList = new ArrayList<>();
}

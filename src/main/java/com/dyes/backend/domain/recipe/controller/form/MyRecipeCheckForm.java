package com.dyes.backend.domain.recipe.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyRecipeCheckForm {
    private String userToken;
}

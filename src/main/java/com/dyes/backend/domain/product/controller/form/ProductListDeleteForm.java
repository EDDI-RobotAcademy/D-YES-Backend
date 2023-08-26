package com.dyes.backend.domain.product.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDeleteForm {
    private String userToken;
    private List<Long> productIdList = new ArrayList<>();
}

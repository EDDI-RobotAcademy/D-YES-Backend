package com.dyes.backend.domain.product.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterForm {
    private String productDescription;
    private String optionName;
    private Long optionPrice;
    private int stock;
    private Long value;
    private String unit;
    private String cultivationMethod;
    private String mainImg;
    private List<String> detailImgs;

}

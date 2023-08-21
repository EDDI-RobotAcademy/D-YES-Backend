package com.dyes.backend.domain.product.service.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseForm {
    private ProductResponse productResponse;
    private List<ProductOptionResponse> OptionList;
    private ProductMainImageResponse mainImage;
    private List<ProductDetailImagesResponse> detailImagesList;
}

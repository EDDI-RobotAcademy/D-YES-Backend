package com.dyes.backend.domain.product.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProductResponseForm {
    private ProductResponse productResponse;
    private List<ProductOptionResponse> OptionList;
    private ProductMainImageResponse mainImage;
    private List<ProductDetailImagesResponse> detailImagesList;
    private FarmInfoResponse farmInfoResponse;
}

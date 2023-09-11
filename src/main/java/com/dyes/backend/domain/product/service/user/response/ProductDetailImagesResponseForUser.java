package com.dyes.backend.domain.product.service.user.response;

import com.dyes.backend.domain.product.entity.ProductDetailImages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailImagesResponseForUser {
    private Long detailImageId;
    private String detailImgs;

    public ProductDetailImagesResponseForUser(ProductDetailImages productDetailImages) {
        this.detailImageId = productDetailImages.getId();
        this.detailImgs = productDetailImages.getDetailImgs();
    }

    public List<ProductDetailImagesResponseForUser> productDetailImagesResponseList(List<ProductDetailImages> productDetailImagesList) {
        return productDetailImagesList.stream()
                .map(ProductDetailImagesResponseForUser::new)
                .collect(Collectors.toList());
    }
}

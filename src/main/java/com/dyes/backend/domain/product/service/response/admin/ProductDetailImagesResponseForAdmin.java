package com.dyes.backend.domain.product.service.response.admin;

import com.dyes.backend.domain.product.entity.ProductDetailImages;
import com.dyes.backend.domain.product.service.response.ProductDetailImagesResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailImagesResponseForAdmin {
    private Long detailImageId;
    private String detailImgs;
    public ProductDetailImagesResponseForAdmin(ProductDetailImages productDetailImages) {
        this.detailImageId = productDetailImages.getId();
        this.detailImgs = productDetailImages.getDetailImgs();
    }
    public List<ProductDetailImagesResponseForAdmin> productDetailImagesResponseForAdminList(List<ProductDetailImages> productDetailImagesList) {
        return productDetailImagesList.stream()
                .map(ProductDetailImagesResponseForAdmin::new)
                .collect(Collectors.toList());
    }
}

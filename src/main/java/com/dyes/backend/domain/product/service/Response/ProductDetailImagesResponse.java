package com.dyes.backend.domain.product.service.Response;

import com.dyes.backend.domain.product.entity.ProductDetailImages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailImagesResponse {
    private Long detailImageId;
    private String detailImgs;
    public ProductDetailImagesResponse(ProductDetailImages productDetailImages) {
        this.detailImageId = productDetailImages.getId();
        this.detailImgs = productDetailImages.getDetailImgs();
    }
    public List<ProductDetailImagesResponse> productDetailImagesResponseList(List<ProductDetailImages> productDetailImagesList) {
        return productDetailImagesList.stream()
                .map(ProductDetailImagesResponse::new)
                .collect(Collectors.toList());
    }
}

package com.dyes.backend.domain.product.service.response;

import com.dyes.backend.domain.product.entity.ProductMainImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMainImageResponse {
    private Long mainImageId;
    private String mainImg;
    public ProductMainImageResponse productMainImageResponse (ProductMainImage productMainImage) {
        return new ProductMainImageResponse(productMainImage.getId(), productMainImage.getMainImg());
    }
}

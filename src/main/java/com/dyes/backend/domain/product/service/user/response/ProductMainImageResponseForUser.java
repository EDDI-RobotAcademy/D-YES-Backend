package com.dyes.backend.domain.product.service.user.response;

import com.dyes.backend.domain.product.entity.ProductMainImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMainImageResponseForUser {
    private Long mainImageId;
    private String mainImg;

    public ProductMainImageResponseForUser productMainImageResponse(ProductMainImage productMainImage) {
        return new ProductMainImageResponseForUser(productMainImage.getId(), productMainImage.getMainImg());
    }
}

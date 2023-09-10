package com.dyes.backend.domain.product.service.admin.response;

import com.dyes.backend.domain.product.entity.ProductMainImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMainImageResponseForAdmin {
    private Long mainImageId;
    private String mainImg;

    public ProductMainImageResponseForAdmin productMainImageResponseForAdmin(ProductMainImage productMainImage) {
        return new ProductMainImageResponseForAdmin(productMainImage.getId(), productMainImage.getMainImg());
    }
}

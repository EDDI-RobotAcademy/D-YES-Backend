package com.dyes.backend.domain.product.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailImagesModifyRequest {
    private Long productDetailImageId;
    private String detailImgs;
}

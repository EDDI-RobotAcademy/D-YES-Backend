package com.dyes.backend.domain.product.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMainImageModifyRequest {
    private Long productMainImageId;
    private String mainImg;
}

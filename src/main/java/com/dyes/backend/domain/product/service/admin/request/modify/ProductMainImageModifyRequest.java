package com.dyes.backend.domain.product.service.admin.request.modify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMainImageModifyRequest {
    private Long mainImageId;
    private String mainImg;
}

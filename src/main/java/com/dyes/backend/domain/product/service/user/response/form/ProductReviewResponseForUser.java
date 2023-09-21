package com.dyes.backend.domain.product.service.user.response.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponseForUser {
    private Integer totalReviewCount;
    private Integer averageRating;
}

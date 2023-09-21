package com.dyes.backend.domain.product.service.user.response.form;

import com.dyes.backend.domain.product.service.user.response.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponseFormForUser {
    private ProductResponseForListForUser productResponseForListForUser;
    private ProductMainImageResponseForListForUser productMainImageResponseForListForUser;
    private ProductOptionResponseForListForUser productOptionResponseForListForUser;
    private FarmInfoResponseForListForUser farmInfoResponseForListForUser;
    private ProductReviewResponseForUser productReviewResponseForUser;
    private FarmProducePriceChangeInfoForListForUser farmProducePriceChangeInfoForListForUser;
}

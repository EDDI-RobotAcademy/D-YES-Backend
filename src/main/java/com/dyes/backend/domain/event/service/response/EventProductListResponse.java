package com.dyes.backend.domain.event.service.response;

import com.dyes.backend.domain.product.service.user.response.*;
import com.dyes.backend.domain.product.service.user.response.form.ProductReviewResponseForUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventProductListResponse {
    private ProductResponseForListForUser productResponseForListForUser;
    private ProductMainImageResponseForListForUser productMainImageResponseForListForUser;
    private ProductOptionResponseForListForUser productOptionResponseForListForUser;
    private FarmInfoResponseForListForUser farmInfoResponseForListForUser;
    private ProductReviewResponseForUser productReviewResponseForUser;
    private EventProductDeadLineResponse deadLineResponse;
    private EventProductPurchaseCountResponse countResponse;
    private EventProductIdResponse eventProductIdResponse;
}

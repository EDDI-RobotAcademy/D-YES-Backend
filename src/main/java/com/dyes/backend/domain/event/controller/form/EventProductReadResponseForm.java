package com.dyes.backend.domain.event.controller.form;

import com.dyes.backend.domain.event.entity.EventDeadLine;
import com.dyes.backend.domain.event.service.response.EventProductDeadLineResponse;
import com.dyes.backend.domain.event.service.response.EventProductProduceTypeResponse;
import com.dyes.backend.domain.event.service.response.EventProductPurchaseCountResponse;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForUser;
import com.dyes.backend.domain.product.service.user.response.ProductDetailImagesResponseForUser;
import com.dyes.backend.domain.product.service.user.response.ProductMainImageResponseForUser;
import com.dyes.backend.domain.product.service.user.response.ProductOptionResponseForUser;
import com.dyes.backend.domain.product.service.user.response.ProductResponseForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReviewResponseForUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventProductReadResponseForm {
    private ProductResponseForUser productResponseForUser;
    private EventProductProduceTypeResponse eventProductProduceTypeResponse;
    private ProductOptionResponseForUser optionResponseForUser;
    private ProductMainImageResponseForUser mainImageResponseForUser;
    private List<ProductDetailImagesResponseForUser> detailImagesForUser;
    private FarmInfoResponseForUser farmInfoResponseForUser;
    private ProductReviewResponseForUser productReviewResponseForUser;
    private EventProductDeadLineResponse eventProductDeadLineResponse;
    private EventProductPurchaseCountResponse eventProductPurchaseCountResponse;
}

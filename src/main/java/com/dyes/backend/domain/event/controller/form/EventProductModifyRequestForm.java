package com.dyes.backend.domain.event.controller.form;

import com.dyes.backend.domain.event.service.request.modify.EventProductModifyDeadLineRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.modify.ProductModifyUserTokenAndEventProductIdRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductModifyRequestForm {
    private ProductModifyUserTokenAndEventProductIdRequest productModifyUserTokenAndEventProductIdRequest;
    private ProductModifyRequest productModifyRequest;
    private ProductOptionModifyRequest productOptionModifyRequest;
    private ProductMainImageModifyRequest productMainImageModifyRequest;
    private List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequest;
    private EventProductModifyDeadLineRequest eventProductModifyDeadLineRequest;
    private EventProductModifyPurchaseCountRequest eventProductModifyPurchaseCountRequest;
}

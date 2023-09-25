package com.dyes.backend.domain.event.service;

import com.dyes.backend.domain.event.controller.form.EventProductReadResponseForm;
import com.dyes.backend.domain.event.service.request.delete.EventProductDeleteRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyDeadLineRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.modify.ProductModifyUserTokenAndEventProductIdRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.EventProductAdminListResponse;
import com.dyes.backend.domain.event.service.response.EventProductListResponse;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;

import java.util.List;

public interface EventService {
    boolean eventProductRegister(EventProductRegisterRequest productRequest,
                                 EventProductRegisterDeadLineRequest deadLineRequest,
                                 EventProductRegisterPurchaseCountRequest countRequest);
    List<EventProductListResponse> eventProductList();
    EventProductReadResponseForm eventProductRead(Long eventProductId);
    boolean eventProductModify(ProductModifyUserTokenAndEventProductIdRequest productModifyUserTokenAndEventProductIdRequest,
                               ProductModifyRequest productModifyRequest, ProductOptionModifyRequest productOptionModifyRequest,
                               ProductMainImageModifyRequest productMainImageModifyRequest, List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequest,
                               EventProductModifyDeadLineRequest eventProductModifyDeadLineRequest, EventProductModifyPurchaseCountRequest eventProductModifyPurchaseCountRequest);
    boolean eventProductDelete(EventProductDeleteRequest request);
    List<EventProductAdminListResponse> eventProductAdminList();
}

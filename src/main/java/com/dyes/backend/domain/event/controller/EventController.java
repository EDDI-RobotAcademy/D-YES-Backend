package com.dyes.backend.domain.event.controller;

import com.dyes.backend.domain.event.controller.form.*;
import com.dyes.backend.domain.event.service.EventService;
import com.dyes.backend.domain.event.service.request.delete.EventProductDeleteRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyDeadLineRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.modify.ProductModifyUserTokenAndEventProductIdRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.EventProductListResponse;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    final private EventService eventService;

    // 관리자의 공동 구매 상품 등록
    @PostMapping("/register")
    public boolean adminRegisterEventProduct(@RequestBody EventProductRegisterRequestForm requestForm) {
        EventProductRegisterRequest registerRequest = requestForm.getEventProductRegisterRequest();
        EventProductRegisterDeadLineRequest deadLineRequest = requestForm.getEventProductRegisterDeadLineRequest();
        EventProductRegisterPurchaseCountRequest countRequest = requestForm.getEventProductRegisterPurchaseCountRequest();

        return eventService.eventProductRegister(registerRequest, deadLineRequest, countRequest);
    }

    // 공동 구매 상품의 목록 보기
    @GetMapping("/list/all")
    public EventProductListResponseForm eventProductList() {
        List<EventProductListResponse> responseList = eventService.eventProductList();
        EventProductListResponseForm responseForm = new EventProductListResponseForm(responseList);

        return responseForm;
    }

    // 공동 구매 상품의 내용 보기
    @GetMapping("/read/{eventProductId}")
    public EventProductReadResponseForm eventProductRead(@PathVariable Long eventProductId) {
        return eventService.eventProductRead(eventProductId);
    }
    @PostMapping("/modify")
    public boolean eventProductModify(@RequestBody EventProductModifyRequestForm requestForm) {
        ProductModifyUserTokenAndEventProductIdRequest productModifyUserTokenAndEventProductIdRequest = requestForm.getProductModifyUserTokenAndEventProductIdRequest();
        ProductModifyRequest productModifyRequest = requestForm.getProductModifyRequest();
        ProductOptionModifyRequest productOptionModifyRequest = requestForm.getProductOptionModifyRequest();
        ProductMainImageModifyRequest productMainImageModifyRequest = requestForm.getProductMainImageModifyRequest();
        List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequest = requestForm.getProductDetailImagesModifyRequest();
        EventProductModifyDeadLineRequest eventProductModifyDeadLineRequest = requestForm.getEventProductModifyDeadLineRequest();
        EventProductModifyPurchaseCountRequest eventProductModifyPurchaseCountRequest = requestForm.getEventProductModifyPurchaseCountRequest();

        boolean result = eventService.eventProductModify(
                productModifyUserTokenAndEventProductIdRequest, productModifyRequest, productOptionModifyRequest,
                productMainImageModifyRequest, productDetailImagesModifyRequest, eventProductModifyDeadLineRequest,
                eventProductModifyPurchaseCountRequest);

        return result;
    }
    @DeleteMapping("/delete")
    public boolean eventProductModify(@RequestBody EventProductDeleteRequestForm requestForm) {
        EventProductDeleteRequest request = requestForm.getEventProductDeleteRequest();

        boolean result = eventService.eventProductDelete(request);

        return result;
    }
}

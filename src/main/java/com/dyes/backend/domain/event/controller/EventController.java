package com.dyes.backend.domain.event.controller;

import com.dyes.backend.domain.event.controller.form.EventProductListResponseForm;
import com.dyes.backend.domain.event.controller.form.EventProductRegisterRequestForm;
import com.dyes.backend.domain.event.service.EventService;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.EventProductListResponse;
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
}

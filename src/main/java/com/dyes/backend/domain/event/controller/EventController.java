package com.dyes.backend.domain.event.controller;

import com.dyes.backend.domain.event.controller.form.EventProductRegisterRequestForm;
import com.dyes.backend.domain.event.service.EventService;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

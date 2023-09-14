package com.dyes.backend.domain.delivery.controller;

import com.dyes.backend.domain.delivery.controller.form.DeliveryStatusChangeRequestForm;
import com.dyes.backend.domain.delivery.service.DeliveryService;
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
@RequestMapping("/delivery")
public class DeliveryController {
    final private DeliveryService deliveryService;

    // 배송 상태 변경
    @PostMapping("/change-status")
    public Boolean registerFarm(@RequestBody DeliveryStatusChangeRequestForm changeRequestForm) {
        return deliveryService.changeStatus(changeRequestForm);
    }
}

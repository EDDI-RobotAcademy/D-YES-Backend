package com.dyes.backend.domain.delivery.controller.form;

import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import com.dyes.backend.domain.delivery.service.request.DeliveryStatusChangeRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusChangeRequestForm {

    // 관리자 여부 확인용
    private String userToken;
    private String productOrderId;
    private DeliveryStatus deliveryStatus;
    private LocalDate deliveryDate;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public DeliveryStatusChangeRequest toDeliveryStatusChangeRequest() {
        return new DeliveryStatusChangeRequest(productOrderId, deliveryStatus, deliveryDate);
    }
}

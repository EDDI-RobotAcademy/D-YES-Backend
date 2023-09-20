package com.dyes.backend.domain.delivery.service.request;

import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusChangeRequest {
    private Long productOrderId;

    private DeliveryStatus deliveryStatus;

    private LocalDate deliveryDate;
}

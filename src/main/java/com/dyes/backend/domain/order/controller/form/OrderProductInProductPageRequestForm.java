package com.dyes.backend.domain.order.controller.form;

import com.dyes.backend.domain.order.service.request.OrderProductInProductPageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductInProductPageRequestForm {
    private String userToken;
    private OrderProductInProductPageRequest request;
}

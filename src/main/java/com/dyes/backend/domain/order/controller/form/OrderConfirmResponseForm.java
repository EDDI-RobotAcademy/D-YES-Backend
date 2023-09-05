package com.dyes.backend.domain.order.controller.form;

import com.dyes.backend.domain.order.service.response.OrderConfirmProductResponse;
import com.dyes.backend.domain.order.service.response.OrderConfirmUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmResponseForm {
    private OrderConfirmUserResponse userResponse;
    private List<OrderConfirmProductResponse> productResponseList;
}

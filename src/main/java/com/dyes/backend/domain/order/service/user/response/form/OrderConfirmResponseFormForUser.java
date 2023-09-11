package com.dyes.backend.domain.order.service.user.response.form;

import com.dyes.backend.domain.order.service.user.response.OrderConfirmProductResponse;
import com.dyes.backend.domain.order.service.user.response.OrderConfirmUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmResponseFormForUser {
    private OrderConfirmUserResponse userResponse;
    private List<OrderConfirmProductResponse> productResponseList;
}

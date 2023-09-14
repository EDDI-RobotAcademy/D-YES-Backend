package com.dyes.backend.domain.order.controller.form;

import com.dyes.backend.domain.order.service.user.request.OrderConfirmProductRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmRequestForm {
    private String userToken;

    private List<OrderConfirmProductRequest> requestList;
}

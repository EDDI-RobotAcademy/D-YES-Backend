package com.dyes.backend.domain.cart.controller.form;

import com.dyes.backend.domain.cart.service.request.ContainProductModifyRequest;
import com.dyes.backend.domain.cart.service.request.ContainProductOptionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainProductModifyRequestForm {
    private String userToken;
    private ContainProductModifyRequest request;
}

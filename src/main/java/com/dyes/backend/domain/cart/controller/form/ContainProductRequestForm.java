package com.dyes.backend.domain.cart.controller.form;

import com.dyes.backend.domain.cart.service.request.ContainProductOptionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainProductRequestForm {
    private String userToken;
    private List<ContainProductOptionRequest> requestList;
}

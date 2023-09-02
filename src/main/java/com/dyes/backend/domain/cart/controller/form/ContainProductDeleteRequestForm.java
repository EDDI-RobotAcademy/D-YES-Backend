package com.dyes.backend.domain.cart.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainProductDeleteRequestForm {
    private String userToken;
    private Long productOptionId;
}

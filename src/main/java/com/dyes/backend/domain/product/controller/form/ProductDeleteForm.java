package com.dyes.backend.domain.product.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeleteForm {
    private String userToken;
    private Long productId;
}

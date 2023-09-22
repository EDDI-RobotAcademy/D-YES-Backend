package com.dyes.backend.domain.product.service.admin.response;

import com.dyes.backend.domain.product.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponseForAdmin {
    private Long productId;
    private String productName;
    private SaleStatus productSaleStatus;
    private LocalDate registrationDate;
}

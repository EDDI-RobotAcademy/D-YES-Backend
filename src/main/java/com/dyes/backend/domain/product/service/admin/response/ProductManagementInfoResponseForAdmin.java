package com.dyes.backend.domain.product.service.admin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductManagementInfoResponseForAdmin {
    private LocalDate registrationDate;
    private int registeredUserCount;
}

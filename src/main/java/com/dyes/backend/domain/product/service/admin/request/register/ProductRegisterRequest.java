package com.dyes.backend.domain.product.service.admin.request.register;

import com.dyes.backend.domain.product.entity.CultivationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterRequest {
    private String productName;
    private String productDescription;
    private CultivationMethod cultivationMethod;
}

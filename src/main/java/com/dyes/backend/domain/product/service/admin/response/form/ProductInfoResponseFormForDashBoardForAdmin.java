package com.dyes.backend.domain.product.service.admin.response.form;

import com.dyes.backend.domain.product.service.admin.response.ProductInfoResponseForAdmin;
import com.dyes.backend.domain.product.service.admin.response.ProductManagementInfoResponseForAdmin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponseFormForDashBoardForAdmin {
    private List<ProductInfoResponseForAdmin> productInfoResponseForAdminList;
    private List<ProductManagementInfoResponseForAdmin> registeredProductCountList;
}

package com.dyes.backend.domain.product.service.request;

import com.dyes.backend.domain.product.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterRequest {
    private String productName;
    private String productDescription;
    private String cultivationMethod;
    private List<ProductOptionRequest> ProductOptionRequest;
    private String mainImg;
    private List<String> detailImgs;
}

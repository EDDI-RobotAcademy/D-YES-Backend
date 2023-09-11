package com.dyes.backend.domain.product.service.admin.request.delete;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDeleteRequest {
    private List<Long> productIdList = new ArrayList<>();
}
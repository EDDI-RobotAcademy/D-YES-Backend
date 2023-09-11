package com.dyes.backend.domain.product.service.admin.response.form;

import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForAdmin;
import com.dyes.backend.domain.farm.service.response.FarmOperationInfoResponseForAdmin;
import com.dyes.backend.domain.product.service.admin.response.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReadResponseFormForAdmin {
    private ProductResponseForAdmin productResponseForAdmin;
    private List<ProductOptionResponseForAdmin> optionResponseForAdmin;
    private ProductMainImageResponseForAdmin mainImageResponseForAdmin;
    private List<ProductDetailImagesResponseForAdmin> detailImagesForAdmin;
    private FarmInfoResponseForAdmin farmInfoResponseForAdmin;
}

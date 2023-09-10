package com.dyes.backend.domain.product.service.user.response.form;

import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForUser;
import com.dyes.backend.domain.product.service.user.response.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReadResponseFormForUser {
    private ProductResponseForUser productResponseForUser;
    private List<ProductOptionResponseForUser> OptionList;
    private ProductMainImageResponseForUser mainImage;
    private List<ProductDetailImagesResponseForUser> detailImagesList;
    private FarmInfoResponseForUser farmInfoResponseForUser;
}

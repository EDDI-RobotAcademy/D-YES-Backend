package com.dyes.backend.domain.product.service.user;

import com.dyes.backend.domain.product.service.user.response.form.RandomProductListResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReadResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductListResponseFormForUser;

import java.util.List;

public interface UserProductService {
    ProductReadResponseFormForUser readProductForUser(Long productId);

    List<ProductListResponseFormForUser> getProductListForUser();

    List<RandomProductListResponseFormForUser> getRandomProductListForUser();

    List<ProductListResponseFormForUser> getProductListByCategoryForUser(String category);

    List<ProductListResponseFormForUser> getProductListByRegionForUser(String region);

    List<ProductListResponseFormForUser> getNew10ProductListForUser();
}

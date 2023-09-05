package com.dyes.backend.domain.cart.service;

import com.dyes.backend.domain.cart.controller.form.ContainProductDeleteRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductListRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductModifyRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.service.reponse.ContainProductCountChangeResponse;
import com.dyes.backend.domain.cart.service.reponse.ContainProductListResponse;
import com.dyes.backend.domain.product.entity.ProductOption;

import java.util.List;

public interface CartService {
    void containProductIntoCart(ContainProductRequestForm requestForm);
    ContainProductCountChangeResponse changeProductOptionCount(ContainProductModifyRequestForm requestForm);
    void deleteProductOptionInCart(List<ContainProductDeleteRequestForm> requestFormList);
    List<ContainProductListResponse> productListResponse (ContainProductListRequestForm requestForm);
    Cart cartCheckFromUserToken(String userToken);
    ProductOption isReallyExistProductOption(Long productOptionId);
}

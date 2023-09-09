package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.product.controller.form.ProductDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductListDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.entity.CultivationMethod;
import com.dyes.backend.domain.product.service.response.UserRandomProductListResponseForm;
import com.dyes.backend.domain.product.service.response.admin.AdminProductListResponseForm;
import com.dyes.backend.domain.product.service.response.admin.ProductResponseFormForAdmin;
import com.dyes.backend.domain.product.service.response.UserProductResponseForm;
import com.dyes.backend.domain.product.service.response.UserProductListResponseForm;
import com.dyes.backend.domain.product.service.response.admin.ProductSummaryResponseFormForAdmin;

import java.util.List;

public interface ProductService {
    boolean productRegistration(ProductRegisterForm registerForm);
    ProductResponseFormForAdmin readProductForAdmin(Long productId);
    UserProductResponseForm readProduct(Long productId);
    boolean productModify(Long productId, ProductModifyForm modifyForm);
    boolean productDelete(ProductDeleteForm deleteForm);
    boolean productListDelete(ProductListDeleteForm listDeleteForm);
    List<AdminProductListResponseForm> getAdminProductList(String userToken);
    List<UserProductListResponseForm> getUserProductList();
    ProductSummaryResponseFormForAdmin readProductSummaryForAdmin(Long productId);
    List<UserRandomProductListResponseForm> getUserRandomProductList();
    List<UserProductListResponseForm> getUserProductListByCategory(String categoryName);
    List<UserProductListResponseForm> getUserProductListByRegion(String region);
}

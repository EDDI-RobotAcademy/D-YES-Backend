package com.dyes.backend.domain.product.service.admin;

import com.dyes.backend.domain.product.controller.admin.form.ProductDeleteRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductListDeleteRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductModifyRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductRegisterRequestForm;
import com.dyes.backend.domain.product.service.admin.response.form.ProductInfoResponseFormForDashBoardForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductListResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductReadResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductSummaryReadResponseFormForAdmin;

import java.util.List;

public interface AdminProductService {
    boolean registerProduct(ProductRegisterRequestForm registerForm);

    boolean modifyProduct(Long productId, ProductModifyRequestForm modifyForm);

    boolean deleteProduct(Long productId, ProductDeleteRequestForm deleteForm);

    boolean deleteProductList(ProductListDeleteRequestForm listDeleteForm);

    ProductReadResponseFormForAdmin readProductForAdmin(Long productId);

    List<ProductListResponseFormForAdmin> getProductListForAdmin();

    ProductSummaryReadResponseFormForAdmin readProductSummaryForAdmin(Long productId);

    ProductInfoResponseFormForDashBoardForAdmin getNewProductListForAdmin();
}

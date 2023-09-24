package com.dyes.backend.domain.product.controller.admin;

import com.dyes.backend.domain.product.controller.admin.form.ProductDeleteRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductListDeleteRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductModifyRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductRegisterRequestForm;
import com.dyes.backend.domain.product.service.admin.AdminProductService;
import com.dyes.backend.domain.product.service.admin.response.form.ProductInfoResponseFormForDashBoardForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductListResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductReadResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductSummaryReadResponseFormForAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class AdminProductController {
    final private AdminProductService adminProductService;

    // 관리자용
    // 1. 상품 등록
    @PostMapping("/admin/register")
    public boolean registerProduct(@RequestBody ProductRegisterRequestForm registerForm) {
        return adminProductService.registerProduct(registerForm);
    }

    // 2. 상품 읽기
    @GetMapping("/admin/read/{productId}")
    public ProductReadResponseFormForAdmin readProductForAdmin(@PathVariable("productId") Long productId) {
        return adminProductService.readProductForAdmin(productId);
    }

    // 3. 상품 수정
    @PutMapping("/admin/modify/{productId}")
    public boolean modifyProduct(@PathVariable("productId") Long productId,
                                 @RequestBody ProductModifyRequestForm modifyForm) {
        return adminProductService.modifyProduct(productId, modifyForm);
    }

    // 4. 상품 삭제
    @DeleteMapping("/admin/delete/{productId}")
    public boolean deleteProduct(
            @PathVariable("productId") Long productId,
            @RequestBody ProductDeleteRequestForm deleteForm) {
        return adminProductService.deleteProduct(productId, deleteForm);
    }

    // 5. 상품 여러 개 삭제
    @DeleteMapping("/admin/deleteList")
    public boolean deleteProductList(@RequestBody ProductListDeleteRequestForm listDeleteForm) {
        return adminProductService.deleteProductList(listDeleteForm);
    }

    // 6. 상품 목록 조회
    @GetMapping("/admin/list")
    public List<ProductListResponseFormForAdmin> getProductListForAdmin() {
        return adminProductService.getProductListForAdmin();
    }

    // 7. 상품 삭제 전 요약정보 확인
    @GetMapping("/admin/read-summary/{productId}")
    public ProductSummaryReadResponseFormForAdmin readProductSummaryForAdmin(@PathVariable("productId") Long productId) {
        return adminProductService.readProductSummaryForAdmin(productId);
    }

    // 8. 신규 상품 목록 조회
    @GetMapping("/admin/new-list")
    public ProductInfoResponseFormForDashBoardForAdmin getNewProductListForAdmin() {
        return adminProductService.getNewProductListForAdmin();
    }

}

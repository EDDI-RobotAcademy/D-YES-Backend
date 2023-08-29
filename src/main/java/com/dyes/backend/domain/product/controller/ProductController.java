package com.dyes.backend.domain.product.controller;

import com.dyes.backend.domain.product.controller.form.ProductDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductListDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.ProductService;
import com.dyes.backend.domain.product.service.response.admin.AdminProductListResponseForm;
import com.dyes.backend.domain.product.service.response.admin.ProductResponseFormForAdmin;
import com.dyes.backend.domain.product.service.response.UserProductResponseForm;
import com.dyes.backend.domain.product.service.response.UserProductListResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    final private ProductService productService;

    // 관리자용
    // 1. 상품 등록
    @PostMapping("/register")
    public boolean productRegister(@RequestBody ProductRegisterForm registerForm) {
        return productService.productRegistration(registerForm);
    }

    // 2. 상품 읽기
    @GetMapping("/admin/read/{productId}")
    public ProductResponseFormForAdmin readProductForAdmin(@PathVariable("productId") Long productId) {
        return productService.readProductForAdmin(productId);
    }

    // 3. 상품 수정
    @PutMapping("/modify/{productId}")
    public boolean productModify(@PathVariable("productId") Long productId,
                                 @RequestBody ProductModifyForm modifyForm) {
        return productService.productModify(productId, modifyForm);
    }

    // 4. 상품 삭제
    @DeleteMapping("/delete")
    public boolean productDelete(@RequestBody ProductDeleteForm deleteForm) {
        return productService.productDelete(deleteForm);
    }

    // 5. 상품 여러 개 삭제
    @DeleteMapping("/deleteList")
    public boolean productDelete(@RequestBody ProductListDeleteForm listDeleteForm) {
        return productService.productListDelete(listDeleteForm);
    }

    // 6. 상품 목록 조회
    @GetMapping("/admin/list")
    public List<AdminProductListResponseForm> getAdminProductList(@RequestParam("userToken") String userToken) {
        return productService.getAdminProductList(userToken);
    }

    // 사용자용
    // 1. 상품 읽기
    @GetMapping("/user/read/{productId}")
    public UserProductResponseForm productRequester(@PathVariable("productId") Long productId) {
        return productService.readProduct(productId);
    }

    // 2. 상품 목록 조회
    @GetMapping("/user/list")
    public List<UserProductListResponseForm> getUserProductList() {
        return productService.getUserProductList();
    }
}

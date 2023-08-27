package com.dyes.backend.domain.product.controller;

import com.dyes.backend.domain.product.controller.form.ProductDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductListDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.ProductService;
import com.dyes.backend.domain.product.service.Response.AdminProductListResponseForm;
import com.dyes.backend.domain.product.service.Response.UserProductResponseForm;
import com.dyes.backend.domain.product.service.Response.UserProductListResponseForm;
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

    // 상품 등록(관리자용)
    @PostMapping("/register")
    public boolean productRegister(@RequestBody ProductRegisterForm registerForm) {
        return productService.productRegistration(registerForm);
    }

    // 상품 읽기(사용자용)
    @GetMapping("/user/read")
    public UserProductResponseForm productRequester(@RequestParam(name = "productId") Long productId) {
        return productService.readProduct(productId);
    }

    // 상품 수정(관리자용)
    @PutMapping("/modify/{productId}")
    public boolean productModify(@PathVariable("productId") Long productId,
                                 @RequestBody ProductModifyForm modifyForm) {
        return productService.productModify(productId, modifyForm);
    }

    // 상품 삭제(관리자용)
    @DeleteMapping("/delete")
    public boolean productDelete(@RequestBody ProductDeleteForm deleteForm) {
        return productService.productDelete(deleteForm);
    }

    // 상품 여러 개 삭제(관리자용)
    @DeleteMapping("/deleteList")
    public boolean productDelete(@RequestBody ProductListDeleteForm listDeleteForm) {
        return productService.productListDelete(listDeleteForm);
    }

    // 상품 목록 조회(관리자용)
    @GetMapping("/admin/list")
    public List<AdminProductListResponseForm> getAdminProductList(@RequestParam("userToken") String userToken) {
        return productService.getAdminProductList(userToken);
    }

    // 상품 목록 조회(사용자용)
    @GetMapping("/user/list")
    public List<UserProductListResponseForm> getUserProductList() {
        return productService.getUserProductList();
    }
}

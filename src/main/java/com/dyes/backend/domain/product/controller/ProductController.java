package com.dyes.backend.domain.product.controller;

import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.ProductService;
import com.dyes.backend.domain.product.service.Response.AdminProductListResponseForm;
import com.dyes.backend.domain.product.service.Response.ProductResponseForm;
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

    // 상품 등록
    @PostMapping("/register")
    public boolean productRegister(@RequestBody ProductRegisterForm registerForm) {
        return productService.productRegistration(registerForm);
    }

    // 상품 읽기
    @GetMapping("/read")
    public ProductResponseForm productRequester(@RequestParam(name = "productId") Long productId) {
        return productService.readProduct(productId);
    }

    // 상품 수정
    @PutMapping("/modify")
    public boolean productModify(@RequestBody ProductModifyForm modifyForm) {
        return productService.productModify(modifyForm);
    }

    // 상품 삭제
    @DeleteMapping("/delete")
    public boolean productDelete(@RequestParam(name = "productId") Long productId) {
        return productService.productDelete(productId);
    }

    // 관리자용 상품 목록 조회
    @GetMapping("/admin/list")
    public List<AdminProductListResponseForm> getAdminProductList(@RequestParam("userToken") String userToken) {
        return productService.getAdminProductList(userToken);
    }
}

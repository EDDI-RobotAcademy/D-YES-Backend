package com.dyes.backend.domain.product.controller;

import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.ProductService;
import com.dyes.backend.domain.product.service.Response.ProductResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    final private ProductService productService;
    @PostMapping("/register")
    public boolean productRegister(@RequestBody ProductRegisterForm registerForm) {
        return productService.productRegistration(registerForm);
    }
    @GetMapping("/read")
    public ProductResponseForm productRequester(@RequestParam(name = "productId") Long productId) {
        return productService.readProduct(productId);
    }
}

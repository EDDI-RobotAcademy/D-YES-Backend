package com.dyes.backend.domain.product.controller.user;

import com.dyes.backend.domain.product.service.user.UserProductService;
import com.dyes.backend.domain.product.service.user.response.form.RandomProductListResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReadResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductListResponseFormForUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class UserProductController {
    final private UserProductService userProductService;

    // 사용자용
    // 1. 상품 읽기
    @GetMapping("/user/read/{productId}")
    public ProductReadResponseFormForUser readProductForUser(@PathVariable("productId") Long productId) {
        return userProductService.readProductForUser(productId);
    }

    // 2. 상품 목록 조회
    @GetMapping("/user/list/all")
    public List<ProductListResponseFormForUser> getProductListForUser() {
        return userProductService.getProductListForUser();
    }

    // 3. 랜덤 상품 4개 조회
    @GetMapping("/user/random-list")
    public List<RandomProductListResponseFormForUser> getRandomProductListForUser() {
        return userProductService.getRandomProductListForUser();
    }

    // 4. 카테고리별 상품 목록 조회
    @GetMapping("/user/list/category/{category}")
    public List<ProductListResponseFormForUser> getProductListByCategoryForUser(
            @PathVariable("category") String category) {
        return userProductService.getProductListByCategoryForUser(category);
    }

    // 5. 농가 지역별 상품 목록 조회
    @GetMapping("/user/list/region/{region}")
    public List<ProductListResponseFormForUser> getProductListByRegionForUser(
            @PathVariable("region") String region) {
        return userProductService.getProductListByRegionForUser(region);
    }
}

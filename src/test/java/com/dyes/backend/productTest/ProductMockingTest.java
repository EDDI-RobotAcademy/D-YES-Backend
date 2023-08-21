package com.dyes.backend.productTest;

import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ProductMockingTest {
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private ProductMainImageRepository mockProductMainImageRepository;
    @Mock
    private ProductDetailImagesRepository mockProductDetailImagesRepository;
    @InjectMocks
    private ProductServiceImpl mockService;
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new ProductServiceImpl(
                mockProductRepository,
                mockProductOptionRepository,
                mockProductMainImageRepository,
                mockProductDetailImagesRepository
        );
    }
    @Test
    @DisplayName("product mocking test: product registration")
    public void 관리자가_상품_등록을_합니다 () {

        final String productDescription = "상세설명";
        final String optionName = "옵션 이름";
        final Long optionPrice = 1L;
        final int stock = 1;
        final Long value = 1L;
        final String unit = "KG";
        final String cultivationMethod = "organic";
        final String mainImage = "메인 이미지";
        final List<String> detailImages = Arrays.asList("디테일 이미지1", "디테일 이미지2");

        ProductRegisterForm registerForm = new ProductRegisterForm(
                productDescription,
                optionName,
                optionPrice,
                stock,
                value,
                unit,
                cultivationMethod,
                mainImage,
                detailImages
                );

        boolean result = mockService.productRegistration(registerForm);
        assertTrue(result);

        verify(mockProductRepository, times(1)).save(any());
        verify(mockProductOptionRepository, times(1)).save(any());
        verify(mockProductMainImageRepository, times(1)).save(any());
        verify(mockProductDetailImagesRepository, times(2)).save(any());
    }

}

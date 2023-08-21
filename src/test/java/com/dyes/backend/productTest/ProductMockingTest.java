package com.dyes.backend.productTest;

import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.request.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.request.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.request.ProductRegisterRequest;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.ProductServiceImpl;
import com.dyes.backend.domain.product.service.request.ProductOptionRegisterRequest;
import com.dyes.backend.domain.product.service.Response.*;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        final String productName = "상품이름";
        final String productDescription = "상세설명";
        final String optionName = "옵션 이름";
        final Long optionPrice = 1L;
        final int stock = 1;
        final Long value = 1L;
        final String unit = "KG";
        final String cultivationMethod = "organic";
        final String mainImage = "메인 이미지";
        final String detailImages = "디테일 이미지1";

        ProductRegisterRequest productRegisterRequest = new ProductRegisterRequest(productName, productDescription, cultivationMethod);
        ProductOptionRegisterRequest productOptionRegisterRequest = new ProductOptionRegisterRequest(optionName, optionPrice, stock, value, unit);
        ProductMainImageRegisterRequest productMainImageRegisterRequest = new ProductMainImageRegisterRequest(mainImage);
        ProductDetailImagesRegisterRequest productDetailImagesRegisterRequest = new ProductDetailImagesRegisterRequest(detailImages);

        ProductRegisterForm registerForm = new ProductRegisterForm(
                productRegisterRequest,
                Arrays.asList(productOptionRegisterRequest),
                productMainImageRegisterRequest,
                Arrays.asList(productDetailImagesRegisterRequest)
                );

        boolean result = mockService.productRegistration(registerForm);
        assertTrue(result);

        verify(mockProductRepository, times(1)).save(any());
        verify(mockProductOptionRepository, times(1)).save(any());
        verify(mockProductMainImageRepository, times(1)).save(any());
        verify(mockProductDetailImagesRepository, times(1)).save(any());
    }
    @Test
    @DisplayName("product mocking test: view product")
    public void 사용자가_상품을_볼_수_있습니다 () {
        final Long productId = 1L;

        Product product = new Product(productId, "상품 이름","상세 설명", CultivationMethod.ORGANIC);
        when(mockProductRepository.findById(productId)).thenReturn(Optional.of(product));

        List<ProductOption> productOption = new ArrayList<>();
        productOption.add(new ProductOption(1L, "옵션 이름", 1L, 1, new Amount(),  product));
        ProductMainImage mainImage = new ProductMainImage(product.getId(), "메인 이미지", product);
        List<ProductDetailImages> detailImages = new ArrayList<>();
        detailImages.add(new ProductDetailImages(1L, "디테일 이미지", product));

        when(mockProductRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOption);
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(mainImage));
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(detailImages);

        ProductResponse productResponse = new ProductResponse().productResponse(product);
        List<ProductOptionResponse> productOptionResponse = new ProductOptionResponse().productOptionResponseList(productOption);
        ProductMainImageResponse productMainImageResponse = new ProductMainImageResponse(mainImage.getId(), mainImage.getMainImg());
        List<ProductDetailImagesResponse> productDetailImagesResponse = new ProductDetailImagesResponse().productDetailImagesResponseList(detailImages);

        ProductResponseForm res = new ProductResponseForm(
                productResponse,
                productOptionResponse,
                productMainImageResponse,
                productDetailImagesResponse
                );

        ProductResponseForm actual = mockService.readProduct(productId);

        assertEquals(res, actual);
    }
}

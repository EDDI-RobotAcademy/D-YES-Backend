package com.dyes.backend.productTest;

import com.dyes.backend.domain.admin.service.AdminServiceImpl;
import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.request.*;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.ProductServiceImpl;
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

import static com.dyes.backend.domain.product.entity.CultivationMethod.ENVIRONMENT_FRIENDLY;
import static com.dyes.backend.domain.product.entity.CultivationMethod.PESTICIDE_FREE;
import static com.dyes.backend.domain.product.entity.Unit.KG;
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
    private AdminServiceImpl mockAdminService;
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new ProductServiceImpl(
                mockProductRepository,
                mockProductOptionRepository,
                mockProductMainImageRepository,
                mockProductDetailImagesRepository,
                mockAdminService
        );
    }
    @Test
    @DisplayName("product mocking test: product register")
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
    @DisplayName("product mocking test: product read")
    public void 사용자가_상품을_볼_수_있습니다 () {
        final Long productId = 1L;

        Product product = new Product(productId, "상품 이름","상세 설명", CultivationMethod.ORGANIC, SaleStatus.AVAILABLE);
        when(mockProductRepository.findById(productId)).thenReturn(Optional.of(product));

        List<ProductOption> productOption = new ArrayList<>();
        productOption.add(new ProductOption(1L, "옵션 이름", 1L, 1, new Amount(),  product, SaleStatus.AVAILABLE));
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

    @Test
    @DisplayName("product mocking test: product modify")
    public void 관리자가_상품_수정을_합니다 () {
        final Long modifyProductId = 1L;
        final String modifyProductName = "수정된 상품명";
        final String modifyProductDescription = "수정된 상세설명";
        final CultivationMethod modifyCultivationMethod = PESTICIDE_FREE;

        final Long modifyMainImageId = 1L;
        final String modifyMainImage = "수정된 메인 이미지";

        final Long modifyDetailImages1Id = 1L;
        final String modifyDetailImages1 = "수정된 디테일 이미지1";
        final Long modifyDetailImages2Id = 2L;
        final String modifyDetailImages2 = "수정된 디테일 이미지2";

        final Long modifyOption1Id = 1L;
        final String modifyOptionName1 = "수정된 옵션명1";
        final Long modifyOptionPrice1 = 10000L;
        final int modifyStock1 = 10;
        final Long modifyValue1 = 1L;
        final Unit modifyUnit1 = KG;

        final Long modifyOption2Id = 2L;
        final String modifyOptionName2 = "수정된 옵션명2";
        final Long modifyOptionPrice2 = 20000L;
        final int modifyStock2 = 20;
        final Long modifyValue2 = 2L;
        final Unit modifyUnit2 = KG;

        Product product = Product.builder()
                            .productName("상품명")
                            .productDescription("상품 설명")
                            .cultivationMethod(ENVIRONMENT_FRIENDLY)
                            .build();

        ProductMainImage productMainImage = ProductMainImage.builder()
                                                .mainImg("메인 이미지")
                                                .build();

        ProductDetailImages productDetailImages1 = ProductDetailImages.builder()
                                                    .detailImgs("상세 이미지 1")
                                                    .build();

        ProductDetailImages productDetailImages2 = ProductDetailImages.builder()
                                                    .detailImgs("상세 이미지 2")
                                                    .build();

        ProductOption productOption1 = ProductOption.builder()
                                        .optionName("옵션 1")
                                        .optionPrice(1000L)
                                        .stock(10)
                                        .amount(new Amount())
                                        .build();

        ProductOption productOption2 = ProductOption.builder()
                                        .optionName("옵션 2")
                                        .optionPrice(2000L)
                                        .stock(20)
                                        .amount(new Amount())
                                        .build();

        ProductModifyRequest productModifyRequest = new ProductModifyRequest(modifyProductId, modifyProductName, modifyProductDescription, modifyCultivationMethod);

        ProductMainImageModifyRequest productMainImageModifyRequest = new ProductMainImageModifyRequest(modifyMainImageId, modifyMainImage);

        List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequestList = new ArrayList<>();

        ProductDetailImagesModifyRequest productDetailImages1ModifyRequest = new ProductDetailImagesModifyRequest(modifyDetailImages1Id, modifyDetailImages1);
        ProductDetailImagesModifyRequest productDetailImages2ModifyRequest = new ProductDetailImagesModifyRequest(modifyDetailImages2Id, modifyDetailImages2);
        productDetailImagesModifyRequestList.add(productDetailImages1ModifyRequest);
        productDetailImagesModifyRequestList.add(productDetailImages2ModifyRequest);

        List<ProductOptionModifyRequest> productOptionModifyRequestList = new ArrayList<>();
        ProductOptionModifyRequest productOption1ModifyRequest = new ProductOptionModifyRequest(modifyOption1Id, modifyOptionName1, modifyOptionPrice1, modifyStock1, modifyValue1, modifyUnit1);
        ProductOptionModifyRequest productOption2ModifyRequest = new ProductOptionModifyRequest(modifyOption2Id, modifyOptionName2, modifyOptionPrice2, modifyStock2, modifyValue2, modifyUnit2);
        productOptionModifyRequestList.add(productOption1ModifyRequest);
        productOptionModifyRequestList.add(productOption2ModifyRequest);

        ProductModifyForm modifyForm = new ProductModifyForm(
                productModifyRequest,
                productOptionModifyRequestList,
                productMainImageModifyRequest,
                productDetailImagesModifyRequestList);

        when(mockProductRepository.findById(modifyProductId)).thenReturn(Optional.of(product));
        when(mockProductMainImageRepository.findById(modifyMainImageId)).thenReturn(Optional.of(productMainImage));
        when(mockProductDetailImagesRepository.findById(modifyDetailImages1Id)).thenReturn(Optional.of(productDetailImages1));
        when(mockProductDetailImagesRepository.findById(modifyDetailImages2Id)).thenReturn(Optional.of(productDetailImages2));
        when(mockProductOptionRepository.findById(modifyOption1Id)).thenReturn(Optional.of(productOption1));
        when(mockProductOptionRepository.findById(modifyOption2Id)).thenReturn(Optional.of(productOption2));

        boolean result = mockService.productModify(modifyForm);
        assertTrue(result);

        verify(mockProductRepository, times(1)).save(any());
        verify(mockProductOptionRepository, times(2)).save(any());
        verify(mockProductMainImageRepository, times(1)).save(any());
        verify(mockProductDetailImagesRepository, times(2)).save(any());

        assertEquals(modifyProductName, product.getProductName());
        assertEquals(modifyProductDescription, product.getProductDescription());
        assertEquals(modifyCultivationMethod, product.getCultivationMethod());

        assertEquals(modifyMainImage, productMainImage.getMainImg());

        assertEquals(modifyDetailImages1, productDetailImages1.getDetailImgs());
        assertEquals(modifyDetailImages2, productDetailImages2.getDetailImgs());

        assertEquals(modifyOptionName1, productOption1.getOptionName());
        assertEquals(modifyOptionPrice1, productOption1.getOptionPrice());
        assertEquals(modifyStock1, productOption1.getStock());
        assertEquals(modifyValue1, productOption1.getAmount().getValue());
        assertEquals(modifyUnit1, productOption1.getAmount().getUnit());

        assertEquals(modifyOptionName2, productOption2.getOptionName());
        assertEquals(modifyOptionPrice2, productOption2.getOptionPrice());
        assertEquals(modifyStock2, productOption2.getStock());
        assertEquals(modifyValue2, productOption2.getAmount().getValue());
        assertEquals(modifyUnit2, productOption2.getAmount().getUnit());
    }

    @Test
    @DisplayName("product mocking test: product delete")
    public void 관리자가_상품을_삭제합니다 () {
        Product product = Product.builder()
                            .id(1L)
                            .productName("상품명")
                            .productDescription("상품 설명")
                            .cultivationMethod(ENVIRONMENT_FRIENDLY)
                            .build();

        List<ProductDetailImages> productDetailImagesList = new ArrayList<>();
        List<ProductOption> productOptionList = new ArrayList<>();

        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(new ProductMainImage()));
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(productDetailImagesList);
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOptionList);

        boolean result = mockService.productDelete(1L);
        assertTrue(result);
    }
}

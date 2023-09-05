package com.dyes.backend.productTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.authentication.service.AuthenticationServiceImpl;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmOperation;
import com.dyes.backend.domain.farm.repository.FarmOperationRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.product.controller.form.ProductDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.request.*;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.ProductServiceImpl;
import com.dyes.backend.domain.product.service.response.*;
import com.dyes.backend.domain.product.service.response.admin.*;
import com.dyes.backend.domain.user.entity.Address;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.redis.RedisServiceImpl;
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
import static com.dyes.backend.domain.product.entity.SaleStatus.AVAILABLE;
import static com.dyes.backend.domain.product.entity.SaleStatus.UNAVAILABLE;
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
    @Mock
    private AdminRepository mockAdminRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private FarmRepository mockFarmRepository;
    @Mock
    private FarmOperationRepository mockFarmOperationRepository;
    @Mock
    private ContainProductOptionRepository mockContainProductOptionRepository;
    @Mock
    private AdminService mockAdminService;
    @Mock
    private AuthenticationServiceImpl mockAuthenticationService;
    @Mock
    private RedisServiceImpl mockRedisService;
    @InjectMocks
    private ProductServiceImpl mockService;
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new ProductServiceImpl(
                mockProductRepository,
                mockProductOptionRepository,
                mockProductMainImageRepository,
                mockProductDetailImagesRepository,
                mockFarmRepository,
                mockFarmOperationRepository,
                mockContainProductOptionRepository,
                mockAdminService
        );
    }
    @Test
    @DisplayName("product mocking test: admin product register")
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
        final String userToken = "mainadmin-dskef3rkewj-welkjw";
        final String farmName = "투투농장";

        ProductRegisterRequest productRegisterRequest = new ProductRegisterRequest(productName, productDescription, cultivationMethod);
        ProductOptionRegisterRequest productOptionRegisterRequest = new ProductOptionRegisterRequest(optionName, optionPrice, stock, value, unit);
        ProductMainImageRegisterRequest productMainImageRegisterRequest = new ProductMainImageRegisterRequest(mainImage);
        ProductDetailImagesRegisterRequest productDetailImagesRegisterRequest = new ProductDetailImagesRegisterRequest(detailImages);

        ProductRegisterForm registerForm = new ProductRegisterForm(
                userToken,
                productRegisterRequest,
                Arrays.asList(productOptionRegisterRequest),
                productMainImageRegisterRequest,
                Arrays.asList(productDetailImagesRegisterRequest),
                farmName
                );

        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(new Admin());
        when(mockUserRepository.findByStringId(anyString())).thenReturn(Optional.of(new User()));
        when(mockAdminRepository.findByUser(new User())).thenReturn(Optional.of(new Admin()));
        when(mockRedisService.getAccessToken(userToken)).thenReturn("accessToken");
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(new User());
        when(mockFarmRepository.findByFarmName(farmName)).thenReturn(Optional.of(new Farm()));

        boolean result = mockService.productRegistration(registerForm);
        assertTrue(result);

        verify(mockProductRepository, times(1)).save(any());
        verify(mockProductOptionRepository, times(1)).save(any());
        verify(mockProductMainImageRepository, times(1)).save(any());
        verify(mockProductDetailImagesRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("product mocking test: user product read")
    public void 사용자가_상품을_볼_수_있습니다 () {
        final Long productId = 1L;

        Product product = new Product(productId, "상품 이름","상세 설명", CultivationMethod.ORGANIC, AVAILABLE, new Farm());
        when(mockProductRepository.findById(productId)).thenReturn(Optional.of(product));

        List<ProductOption> productOption = new ArrayList<>();
        productOption.add(new ProductOption(1L, "옵션 이름", 1L, 1, new Amount(),  product, AVAILABLE));
        ProductMainImage mainImage = new ProductMainImage(product.getId(), "메인 이미지", product);
        List<ProductDetailImages> detailImages = new ArrayList<>();
        detailImages.add(new ProductDetailImages(1L, "디테일 이미지", product));

        when(mockProductRepository.findByIdWithFarm(productId)).thenReturn(Optional.of(product));
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOption);
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(mainImage));
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(detailImages);

        ProductResponse productResponse = new ProductResponse().productResponse(product);
        List<ProductOptionResponse> productOptionResponse = new ProductOptionResponse().productOptionResponseList(productOption);
        ProductMainImageResponse productMainImageResponse = new ProductMainImageResponse(mainImage.getId(), mainImage.getMainImg());
        List<ProductDetailImagesResponse> productDetailImagesResponse = new ProductDetailImagesResponse().productDetailImagesResponseList(detailImages);
        FarmInfoResponse farmInfoResponse = new FarmInfoResponse().farmInfoResponse(new Farm());

        UserProductResponseForm res = new UserProductResponseForm(
                productResponse,
                productOptionResponse,
                productMainImageResponse,
                productDetailImagesResponse,
                farmInfoResponse
                );

        UserProductResponseForm actual = mockService.readProduct(productId);

        assertEquals(res, actual);
    }

    @Test
    @DisplayName("product mocking test: admin product modify")
    public void 관리자가_상품_수정을_합니다 () {
        final Long modifyProductId = 1L;
        final String modifyProductName = "수정된 상품명";
        final String modifyProductDescription = "수정된 상세설명";
        final CultivationMethod modifyCultivationMethod = PESTICIDE_FREE;
        final SaleStatus modifyProductSaleStatus = UNAVAILABLE;

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
        final SaleStatus modifyOption1SaleStatus = UNAVAILABLE;

        final Long modifyOption2Id = 2L;
        final String modifyOptionName2 = "수정된 옵션명2";
        final Long modifyOptionPrice2 = 20000L;
        final int modifyStock2 = 20;
        final Long modifyValue2 = 2L;
        final Unit modifyUnit2 = KG;
        final SaleStatus modifyOption2SaleStatus = UNAVAILABLE;

        final String userToken = "mainadmin-dskef3rkewj-welkjw";

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

        ProductModifyRequest productModifyRequest
                = new ProductModifyRequest(modifyProductName, modifyProductDescription, modifyCultivationMethod, modifyProductSaleStatus);

        ProductMainImageModifyRequest productMainImageModifyRequest
                = new ProductMainImageModifyRequest(modifyMainImageId, modifyMainImage);

        List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequestList = new ArrayList<>();

        ProductDetailImagesModifyRequest productDetailImages1ModifyRequest
                = new ProductDetailImagesModifyRequest(modifyDetailImages1Id, modifyDetailImages1);
        ProductDetailImagesModifyRequest productDetailImages2ModifyRequest
                = new ProductDetailImagesModifyRequest(modifyDetailImages2Id, modifyDetailImages2);
        productDetailImagesModifyRequestList.add(productDetailImages1ModifyRequest);
        productDetailImagesModifyRequestList.add(productDetailImages2ModifyRequest);

        List<ProductOptionModifyRequest> productOptionModifyRequestList = new ArrayList<>();
        ProductOptionModifyRequest productOption1ModifyRequest
                = new ProductOptionModifyRequest(modifyOption1Id, modifyOptionName1, modifyOptionPrice1, modifyStock1, modifyValue1, modifyUnit1, modifyOption1SaleStatus);
        ProductOptionModifyRequest productOption2ModifyRequest
                = new ProductOptionModifyRequest(modifyOption2Id, modifyOptionName2, modifyOptionPrice2, modifyStock2, modifyValue2, modifyUnit2, modifyOption2SaleStatus);
        productOptionModifyRequestList.add(productOption1ModifyRequest);
        productOptionModifyRequestList.add(productOption2ModifyRequest);

        ProductModifyForm modifyForm = new ProductModifyForm(
                userToken,
                productModifyRequest,
                productOptionModifyRequestList,
                productMainImageModifyRequest,
                productDetailImagesModifyRequestList);

        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(new Admin());
        when(mockUserRepository.findByStringId(anyString())).thenReturn(Optional.of(new User()));
        when(mockAdminRepository.findByUser(new User())).thenReturn(Optional.of(new Admin()));
        when(mockRedisService.getAccessToken(userToken)).thenReturn("accessToken");
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(new User());

        when(mockProductRepository.findById(modifyProductId)).thenReturn(Optional.of(product));
        when(mockProductMainImageRepository.findById(modifyMainImageId)).thenReturn(Optional.of(productMainImage));
        when(mockProductDetailImagesRepository.findById(modifyDetailImages1Id)).thenReturn(Optional.of(productDetailImages1));
        when(mockProductDetailImagesRepository.findById(modifyDetailImages2Id)).thenReturn(Optional.of(productDetailImages2));
        when(mockProductOptionRepository.findById(modifyOption1Id)).thenReturn(Optional.of(productOption1));
        when(mockProductOptionRepository.findById(modifyOption2Id)).thenReturn(Optional.of(productOption2));

        boolean result = mockService.productModify(modifyProductId, modifyForm);
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
    @DisplayName("product mocking test: admin product delete")
    public void 관리자가_상품을_삭제합니다 () {
        final String userToken = "mainadmin-dskef3rkewj-welkjw";
        Product product = Product.builder()
                            .id(1L)
                            .productName("상품명")
                            .productDescription("상품 설명")
                            .cultivationMethod(ENVIRONMENT_FRIENDLY)
                            .build();

        List<ProductDetailImages> productDetailImagesList = new ArrayList<>();
        List<ProductOption> productOptionList = new ArrayList<>();

        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(new Admin());
        when(mockUserRepository.findByStringId(anyString())).thenReturn(Optional.of(new User()));
        when(mockAdminRepository.findByUser(new User())).thenReturn(Optional.of(new Admin()));
        when(mockRedisService.getAccessToken(userToken)).thenReturn("accessToken");
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(new User());

        when(mockProductRepository.findByIdWithFarm(1L)).thenReturn(Optional.of(product));
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(new ProductMainImage()));
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(productDetailImagesList);
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOptionList);

        boolean result = mockService.productDelete(new ProductDeleteForm(userToken, 1L));
        assertTrue(result);
    }

    @Test
    @DisplayName("product mocking test: admin product list")
    public void 관리자가_상품목록을_조회합니다 () {
        final String userToken = "normaladmin-ekjfw3rlkgj-4oi34klng";
        List<Product> productList = new ArrayList<>();
        List<ProductOption> productOptionList = new ArrayList<>();

        Farm farm = Farm.builder()
                .farmName("투투농장")
                .build();

        Product product1 = Product.builder()
                .id(1L)
                .productName("상품명1")
                .productDescription("상품 설명1")
                .cultivationMethod(ENVIRONMENT_FRIENDLY)
                .productSaleStatus(AVAILABLE)
                .farm(farm)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("상품명2")
                .productDescription("상품 설명2")
                .cultivationMethod(ENVIRONMENT_FRIENDLY)
                .productSaleStatus(UNAVAILABLE)
                .farm(farm)
                .build();

        productList.add(product1);
        productList.add(product2);

        ProductOption productOption1 = ProductOption.builder()
                .optionName("상품옵션1")
                .optionPrice(13000L)
                .stock(20)
                .amount(new Amount())
                .product(product1)
                .optionSaleStatus(AVAILABLE)
                .build();

        ProductOption productOption2 = ProductOption.builder()
                .optionName("상품옵션2")
                .optionPrice(15000L)
                .stock(70)
                .amount(new Amount())
                .product(product2)
                .optionSaleStatus(AVAILABLE)
                .build();

        productOptionList.add(productOption1);
        productOptionList.add(productOption2);

        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(new Admin());
        when(mockUserRepository.findByStringId(anyString())).thenReturn(Optional.of(new User()));
        when(mockAdminRepository.findByUser(new User())).thenReturn(Optional.of(new Admin()));
        when(mockProductRepository.findAllWithFarm()).thenReturn(productList);
        when(mockProductOptionRepository.findByProduct(productList.get(0))).thenReturn(productOptionList);
        when(mockProductOptionRepository.findByProduct(productList.get(1))).thenReturn(productOptionList);
        when(mockRedisService.getAccessToken(userToken)).thenReturn("accessToken");
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(new User());

        List<AdminProductListResponseForm> result = mockService.getAdminProductList(userToken);

        assertEquals(result.get(0).getProductName(), "상품명1");
        assertEquals(result.get(0).getProductId(), 1L);
        assertEquals(result.get(0).getProductSaleStatus(), AVAILABLE);
        assertEquals(result.get(0).getProductOptionListResponse().get(0).getOptionName(), "상품옵션1");
        assertEquals(result.get(0).getProductOptionListResponse().get(0).getStock(), 20);
        assertEquals(result.get(0).getFarmName(), "투투농장");

        assertEquals(result.get(1).getProductName(), "상품명2");
        assertEquals(result.get(1).getProductId(), 2L);
        assertEquals(result.get(1).getProductSaleStatus(), UNAVAILABLE);
        assertEquals(result.get(1).getProductOptionListResponse().get(1).getOptionName(), "상품옵션2");
        assertEquals(result.get(1).getProductOptionListResponse().get(1).getStock(), 70);
        assertEquals(result.get(1).getFarmName(), "투투농장");
    }

    @Test
    @DisplayName("product mocking test: user product list")
    public void 사용자가_상품목록을_조회합니다 () {
        List<Product> productList = new ArrayList<>();
        List<ProductOption> productOptionList = new ArrayList<>();

        Farm farm = Farm.builder()
                .farmName("투투농장")
                .mainImage("mainImage")
                .build();

        FarmOperation farmOperation = FarmOperation.builder()
                .id(farm.getId())
                .representativeName("정다운")
                .farm(farm)
                .build();

        Product product1 = Product.builder()
                .id(1L)
                .productName("상품명1")
                .productDescription("상품 설명1")
                .cultivationMethod(ENVIRONMENT_FRIENDLY)
                .productSaleStatus(AVAILABLE)
                .farm(farm)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("상품명2")
                .productDescription("상품 설명2")
                .cultivationMethod(ENVIRONMENT_FRIENDLY)
                .productSaleStatus(UNAVAILABLE)
                .farm(farm)
                .build();

        productList.add(product1);
        productList.add(product2);

        ProductOption productOption1 = ProductOption.builder()
                .optionName("상품옵션1")
                .optionPrice(13000L)
                .stock(20)
                .amount(new Amount())
                .product(product1)
                .optionSaleStatus(AVAILABLE)
                .build();

        ProductOption productOption2 = ProductOption.builder()
                .optionName("상품옵션2")
                .optionPrice(15000L)
                .stock(70)
                .amount(new Amount())
                .product(product2)
                .optionSaleStatus(AVAILABLE)
                .build();

        productOptionList.add(productOption1);
        productOptionList.add(productOption2);

        when(mockProductRepository.findAllWithFarm()).thenReturn(productList);
        when(mockProductOptionRepository.findByProduct(productList.get(0))).thenReturn(productOptionList);
        when(mockProductOptionRepository.findByProduct(productList.get(1))).thenReturn(productOptionList);
        when(mockFarmOperationRepository.findByFarm(farm)).thenReturn(farmOperation);

        List<UserProductListResponseForm> result = mockService.getUserProductList();

        assertEquals(result.get(0).getProductName(), "상품명1");
        assertEquals(result.get(0).getProductId(), 1L);
        assertEquals(result.get(0).getIsSoldOut(), false);
        assertEquals(result.get(0).getMinOptionPrice(), 13000L);
        assertEquals(result.get(0).getFarmName(), "투투농장");
        assertEquals(result.get(0).getMainImage(), "mainImage");
        assertEquals(result.get(0).getRepresentativeName(), "정다운");

        assertEquals(result.get(1).getProductName(), "상품명2");
        assertEquals(result.get(1).getProductId(), 2L);
        assertEquals(result.get(1).getIsSoldOut(), false);
        assertEquals(result.get(1).getMinOptionPrice(), 13000L);
        assertEquals(result.get(0).getFarmName(), "투투농장");
        assertEquals(result.get(0).getMainImage(), "mainImage");
        assertEquals(result.get(0).getRepresentativeName(), "정다운");
    }

    @Test
    @DisplayName("product mocking test: admin product read")
    public void 관리자가_상품을_수정하기_전에_상품을_확인할_수_있습니다 () {
        Farm farm = new Farm(1L, "투투농가", "070-1234-5678", new Address(), "농가 메인 이미지", "한줄소개", new ArrayList<>());
        Product product = new Product(1L, "상품 이름","상세 설명", CultivationMethod.ORGANIC, AVAILABLE, farm);
        List<ProductOption> productOption = new ArrayList<>();
        productOption.add(new ProductOption(1L, "옵션 이름", 10000L, 100, new Amount(),  product, AVAILABLE));
        ProductMainImage productMainImage = new ProductMainImage(product.getId(), "메인 이미지", product);
        List<ProductDetailImages> productDetailImages = new ArrayList<>();
        productDetailImages.add(new ProductDetailImages(1L, "디테일 이미지", product));
        FarmOperation farmOperation = new FarmOperation(1L, "(주)투투농가", "123-45-67890", "투투쓰", "010-1111-1111", farm);

        when(mockProductRepository.findByIdWithFarm(product.getId())).thenReturn(Optional.of(product));
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOption);
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(productMainImage));
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(productDetailImages);
        when(mockFarmOperationRepository.findByFarm(farm)).thenReturn(farmOperation);

        ProductResponseForAdmin productResponseForAdmin = new ProductResponseForAdmin().productResponseForAdmin(product);
        List<ProductOptionResponseForAdmin> productOptionResponseForAdmin = new ProductOptionResponseForAdmin().productOptionResponseForAdmin(productOption);
        ProductMainImageResponseForAdmin productMainImageResponseForAdmin = new ProductMainImageResponseForAdmin().productMainImageResponseForAdmin(productMainImage);
        List<ProductDetailImagesResponseForAdmin> productDetailImagesResponsesForAdmin = new ProductDetailImagesResponseForAdmin().productDetailImagesResponseForAdminList(productDetailImages);
        FarmInfoResponseForAdmin farmInfoResponseForAdmin = new FarmInfoResponseForAdmin().farmInfoResponseForAdmin(farm);
        FarmOperationInfoResponseForAdmin farmOperationInfoResponseForAdmin = new FarmOperationInfoResponseForAdmin().farmOperationInfoResponseForAdmin(farmOperation);

        ProductResponseFormForAdmin actual
                = new ProductResponseFormForAdmin(
                productResponseForAdmin,
                productOptionResponseForAdmin,
                productMainImageResponseForAdmin,
                productDetailImagesResponsesForAdmin,
                farmInfoResponseForAdmin,
                farmOperationInfoResponseForAdmin);

        ProductResponseFormForAdmin result = mockService.readProductForAdmin(product.getId());
        assertEquals(result, actual);
    }
}

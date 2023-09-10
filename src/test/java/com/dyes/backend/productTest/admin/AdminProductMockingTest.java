package com.dyes.backend.productTest.admin;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.authentication.service.AuthenticationServiceImpl;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.*;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForAdmin;
import com.dyes.backend.domain.farm.service.response.FarmOperationInfoResponseForAdmin;
import com.dyes.backend.domain.product.controller.admin.form.ProductDeleteRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductModifyRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductRegisterRequestForm;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.admin.AdminProductServiceImpl;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductOptionRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductRegisterRequest;
import com.dyes.backend.domain.product.service.admin.response.*;
import com.dyes.backend.domain.product.service.admin.response.form.ProductListResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductReadResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductSummaryReadResponseFormForAdmin;
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

import static com.dyes.backend.domain.product.entity.CultivationMethod.*;
import static com.dyes.backend.domain.product.entity.SaleStatus.AVAILABLE;
import static com.dyes.backend.domain.product.entity.SaleStatus.UNAVAILABLE;
import static com.dyes.backend.domain.product.entity.Unit.KG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminProductMockingTest {
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
    private FarmBusinessInfoRepository mockFarmBusinessInfoRepository;
    @Mock
    private FarmCustomerServiceInfoRepository mockFarmCustomerServiceInfoRepository;
    @Mock
    private FarmIntroductionInfoRepository mockFarmIntroductionInfoRepository;
    @Mock
    private FarmRepresentativeInfoRepository mockFarmRepresentativeInfoRepository;
    @Mock
    private ContainProductOptionRepository mockContainProductOptionRepository;
    @Mock
    private AdminService mockAdminService;
    @Mock
    private AuthenticationServiceImpl mockAuthenticationService;
    @Mock
    private RedisServiceImpl mockRedisService;
    @InjectMocks
    private AdminProductServiceImpl adminProductService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        adminProductService = new AdminProductServiceImpl(
                mockProductRepository,
                mockProductOptionRepository,
                mockProductMainImageRepository,
                mockProductDetailImagesRepository,
                mockFarmRepository,
                mockFarmBusinessInfoRepository,
                mockFarmCustomerServiceInfoRepository,
                mockFarmIntroductionInfoRepository,
                mockFarmRepresentativeInfoRepository,
                mockContainProductOptionRepository,
                mockAdminService);
    }

    @Test
    @DisplayName("product mocking test: admin product register")
    public void 관리자가_상품_등록을_합니다() {
        final String productName = "상품이름";
        final String productDescription = "상세설명";
        final String optionName = "옵션 이름";
        final Long optionPrice = 1L;
        final int stock = 1;
        final Long value = 1L;
        final Unit unit = KG;
        final CultivationMethod cultivationMethod = ORGANIC;
        final String mainImage = "메인 이미지";
        final String detailImages = "디테일 이미지1";
        final String userToken = "mainadmin-dskef3rkewj-welkjw";
        final String farmName = "투투농장";

        ProductRegisterRequest productRegisterRequest = new ProductRegisterRequest(productName, productDescription, cultivationMethod);
        ProductOptionRegisterRequest productOptionRegisterRequest = new ProductOptionRegisterRequest(optionName, optionPrice, stock, value, unit);
        ProductMainImageRegisterRequest productMainImageRegisterRequest = new ProductMainImageRegisterRequest(mainImage);
        ProductDetailImagesRegisterRequest productDetailImagesRegisterRequest = new ProductDetailImagesRegisterRequest(detailImages);

        ProductRegisterRequestForm registerForm = new ProductRegisterRequestForm(
                userToken,
                productRegisterRequest,
                Arrays.asList(productOptionRegisterRequest),
                productMainImageRegisterRequest,
                Arrays.asList(productDetailImagesRegisterRequest),
                farmName);

        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(new Admin());
        when(mockUserRepository.findByStringId(anyString())).thenReturn(Optional.of(new User()));
        when(mockAdminRepository.findByUser(new User())).thenReturn(Optional.of(new Admin()));
        when(mockRedisService.getAccessToken(userToken)).thenReturn("accessToken");
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(new User());
        when(mockFarmRepository.findByFarmName(farmName)).thenReturn(Optional.of(new Farm()));

        boolean result = adminProductService.registerProduct(registerForm);
        assertTrue(result);

        verify(mockProductRepository, times(1)).save(any());
        verify(mockProductOptionRepository, times(1)).save(any());
        verify(mockProductMainImageRepository, times(1)).save(any());
        verify(mockProductDetailImagesRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("product mocking test: admin product modify")
    public void 관리자가_상품_수정을_합니다() {
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

        ProductModifyRequestForm modifyForm = new ProductModifyRequestForm(
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

        boolean result = adminProductService.modifyProduct(modifyProductId, modifyForm);
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
    public void 관리자가_상품을_삭제합니다() {
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

        boolean result = adminProductService.deleteProduct(1L, new ProductDeleteRequestForm(userToken));
        assertTrue(result);
    }

    @Test
    @DisplayName("product mocking test: admin product list")
    public void 관리자가_상품목록을_조회합니다() {
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

        List<ProductListResponseFormForAdmin> result = adminProductService.getProductListForAdmin();

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
    @DisplayName("product mocking test: admin product read")
    public void 관리자가_상품을_수정하기_전에_상품을_확인할_수_있습니다() {
        Farm farm = new Farm(1L, "투투농가");
        Product product = new Product(1L, "상품 이름", "상세 설명", CultivationMethod.ORGANIC, AVAILABLE, farm);
        List<ProductOption> productOption = new ArrayList<>();
        productOption.add(new ProductOption(1L, "옵션 이름", 10000L, 100, new Amount(), product, AVAILABLE));
        ProductMainImage productMainImage = new ProductMainImage(product.getId(), "메인 이미지", product);
        List<ProductDetailImages> productDetailImages = new ArrayList<>();
        productDetailImages.add(new ProductDetailImages(1L, "디테일 이미지", product));
        FarmBusinessInfo farmBusinessInfo = new FarmBusinessInfo(1L, "(주)투투농가", "123-45-67890", farm);
        FarmCustomerServiceInfo farmCustomerServiceInfo = new FarmCustomerServiceInfo(1L, "070-1234-5678", new Address(), farm);
        FarmIntroductionInfo farmIntroductionInfo = new FarmIntroductionInfo(1L, "mainImage", "한 줄 소개", new ArrayList<>(), farm);
        FarmRepresentativeInfo farmRepresentativeInfo = new FarmRepresentativeInfo(1L, "정다운", "010-1234-5678", farm);

        when(mockProductRepository.findByIdWithFarm(product.getId())).thenReturn(Optional.of(product));
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOption);
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(productMainImage));
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(productDetailImages);
        when(mockFarmBusinessInfoRepository.findByFarm(farm)).thenReturn(farmBusinessInfo);
        when(mockFarmCustomerServiceInfoRepository.findByFarm(farm)).thenReturn(farmCustomerServiceInfo);
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);
        when(mockFarmRepresentativeInfoRepository.findByFarm(farm)).thenReturn(farmRepresentativeInfo);

        ProductResponseForAdmin productResponseForAdmin
                = new ProductResponseForAdmin().productResponseForAdmin(product);
        List<ProductOptionResponseForAdmin> productOptionResponseForAdmin
                = new ProductOptionResponseForAdmin().productOptionResponseForAdmin(productOption);
        ProductMainImageResponseForAdmin productMainImageResponseForAdmin
                = new ProductMainImageResponseForAdmin().productMainImageResponseForAdmin(productMainImage);
        List<ProductDetailImagesResponseForAdmin> productDetailImagesResponsesForAdmin
                = new ProductDetailImagesResponseForAdmin().productDetailImagesResponseForAdminList(productDetailImages);
        FarmInfoResponseForAdmin farmInfoResponseForAdmin
                = new FarmInfoResponseForAdmin().farmInfoResponseForAdmin(farm, farmCustomerServiceInfo, farmIntroductionInfo);
        FarmOperationInfoResponseForAdmin farmOperationInfoResponseForAdmin
                = new FarmOperationInfoResponseForAdmin().farmOperationInfoResponseForAdmin(farmBusinessInfo, farmRepresentativeInfo);

        ProductReadResponseFormForAdmin actual
                = new ProductReadResponseFormForAdmin(
                productResponseForAdmin,
                productOptionResponseForAdmin,
                productMainImageResponseForAdmin,
                productDetailImagesResponsesForAdmin,
                farmInfoResponseForAdmin,
                farmOperationInfoResponseForAdmin);

        ProductReadResponseFormForAdmin result = adminProductService.readProductForAdmin(product.getId());
        assertEquals(result, actual);
    }

    @Test
    @DisplayName("product mocking test: admin product read-summary")
    public void 관리자가_상품을_삭제하기_전에_상품_요약정보를_확인할_수_있습니다() {
        Farm farm = new Farm(1L, "투투농가");
        Product product = new Product(1L, "상품 이름", "상세 설명", CultivationMethod.ORGANIC, AVAILABLE, farm);
        List<ProductOption> productOption = new ArrayList<>();
        productOption.add(new ProductOption(1L, "옵션 이름", 1L, 1, new Amount(), product, AVAILABLE));

        when(mockProductRepository.findByIdWithFarm(product.getId())).thenReturn(Optional.of(product));
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOption);

        ProductSummaryReadResponseFormForAdmin result = adminProductService.readProductSummaryForAdmin(product.getId());
        assertEquals(result.getFarmInfoSummaryResponseForAdmin().getFarmName(), "투투농가");
        assertEquals(result.getProductSummaryResponseForAdmin().getProductName(), "상품 이름");
        assertEquals(result.getOptionSummaryResponseForAdmin().get(0).getOptionName(), "옵션 이름");
    }

}

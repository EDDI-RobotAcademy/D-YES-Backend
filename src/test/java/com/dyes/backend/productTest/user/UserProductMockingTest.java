package com.dyes.backend.productTest.user;

import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.*;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForUser;
import com.dyes.backend.domain.farmproducePriceForecast.repository.*;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.user.UserProductServiceImpl;
import com.dyes.backend.domain.product.service.user.response.*;
import com.dyes.backend.domain.product.service.user.response.form.ProductListResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReadResponseFormForUser;
import com.dyes.backend.domain.user.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dyes.backend.domain.farm.entity.ProduceType.ONION;
import static com.dyes.backend.domain.product.entity.CultivationMethod.*;
import static com.dyes.backend.domain.product.entity.SaleStatus.AVAILABLE;
import static com.dyes.backend.domain.product.entity.SaleStatus.UNAVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserProductMockingTest {
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private ProductMainImageRepository mockProductMainImageRepository;
    @Mock
    private ProductDetailImagesRepository mockProductDetailImagesRepository;
    @Mock
    private ProductManagementRepository productManagementRepository;
    @Mock
    private FarmBusinessInfoRepository mockFarmBusinessInfoRepository;
    @Mock
    private FarmCustomerServiceInfoRepository mockFarmCustomerServiceInfoRepository;
    @Mock
    private FarmIntroductionInfoRepository mockFarmIntroductionInfoRepository;
    @Mock
    private FarmRepresentativeInfoRepository mockFarmRepresentativeInfoRepository;
    @Mock
    private CabbagePriceRepository cabbagePriceRepository;
    @Mock
    private CarrotPriceRepository carrotPriceRepository;
    @Mock
    private CucumberPriceRepository cucumberPriceRepository;
    @Mock
    private KimchiCabbagePriceRepository kimchiCabbagePriceRepository;
    @Mock
    private OnionPriceRepository onionPriceRepository;
    @Mock
    private PotatoPriceRepository potatoPriceRepository;
    @Mock
    private WelshOnionPriceRepository welshOnionPriceRepository;
    @Mock
    private YoungPumpkinPriceRepository youngPumpkinPriceRepository;
    @InjectMocks
    private UserProductServiceImpl userProductService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        userProductService = new UserProductServiceImpl(
                mockProductRepository,
                mockProductOptionRepository,
                mockProductMainImageRepository,
                mockProductDetailImagesRepository,
                productManagementRepository,
                mockFarmCustomerServiceInfoRepository,
                mockFarmIntroductionInfoRepository,
                mockFarmRepresentativeInfoRepository,
                cabbagePriceRepository,
                carrotPriceRepository,
                cucumberPriceRepository,
                kimchiCabbagePriceRepository,
                onionPriceRepository,
                potatoPriceRepository,
                welshOnionPriceRepository,
                youngPumpkinPriceRepository
                );
    }

    @Test
    @DisplayName("product mocking test: user product read")
    public void 사용자가_상품을_볼_수_있습니다() {
        final Long productId = 1L;

        Farm farm = new Farm(1L, "투투농가");
        FarmCustomerServiceInfo farmCustomerServiceInfo = new FarmCustomerServiceInfo(1L, "070-1234-5678", new Address(), farm);
        FarmIntroductionInfo farmIntroductionInfo = new FarmIntroductionInfo(1L, "메인이미지", "한줄소개", new ArrayList<>(), farm);

        Product product = new Product(productId, "상품 이름", "상세 설명", CultivationMethod.ORGANIC, ONION, AVAILABLE, farm);
        when(mockProductRepository.findById(productId)).thenReturn(Optional.of(product));

        List<ProductOption> productOption = new ArrayList<>();
        productOption.add(new ProductOption(1L, "옵션 이름", 1L, 1, new Amount(), product, AVAILABLE));
        ProductMainImage mainImage = new ProductMainImage(product.getId(), "메인 이미지", product);
        List<ProductDetailImages> detailImages = new ArrayList<>();
        detailImages.add(new ProductDetailImages(1L, "디테일 이미지", product));

        when(mockProductRepository.findByIdWithFarm(productId)).thenReturn(Optional.of(product));
        when(mockProductOptionRepository.findByProduct(product)).thenReturn(productOption);
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(mainImage));
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(detailImages);
        when(mockFarmCustomerServiceInfoRepository.findByFarm(farm)).thenReturn(farmCustomerServiceInfo);
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);

        ProductResponseForUser productResponseForUser = new ProductResponseForUser().productResponse(product);
        List<ProductOptionResponseForUser> productOptionResponseForUser = new ProductOptionResponseForUser().productOptionResponseList(productOption);
        ProductMainImageResponseForUser productMainImageResponseForUser = new ProductMainImageResponseForUser(mainImage.getId(), mainImage.getMainImg());
        List<ProductDetailImagesResponseForUser> productDetailImagesResponseForUser = new ProductDetailImagesResponseForUser().productDetailImagesResponseList(detailImages);
        FarmInfoResponseForUser farmInfoResponseForUser = new FarmInfoResponseForUser().farmInfoResponse(farm, farmCustomerServiceInfo, farmIntroductionInfo);

        ProductReadResponseFormForUser result = new ProductReadResponseFormForUser(
                productResponseForUser,
                productOptionResponseForUser,
                productMainImageResponseForUser,
                productDetailImagesResponseForUser,
                farmInfoResponseForUser);

        ProductReadResponseFormForUser actual = userProductService.readProductForUser(productId);

        assertEquals(result, actual);
    }

    @Test
    @DisplayName("product mocking test: user product list")
    public void 사용자가_상품목록을_조회합니다() {
        List<Product> productList = new ArrayList<>();
        List<ProductOption> productOptionList = new ArrayList<>();

        Farm farm = Farm.builder()
                .farmName("투투농장")
                .build();

        FarmBusinessInfo farmBusinessInfo = FarmBusinessInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        Product product1 = Product.builder()
                .id(1L)
                .productName("상품명1")
                .productDescription("상품 설명1")
                .cultivationMethod(ENVIRONMENT_FRIENDLY)
                .produceType(ONION)
                .productSaleStatus(AVAILABLE)
                .farm(farm)
                .build();

        ProductMainImage productMainImage1 = ProductMainImage.builder()
                .mainImg("메인이미지1")
                .product(product1)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("상품명2")
                .productDescription("상품 설명2")
                .cultivationMethod(ENVIRONMENT_FRIENDLY)
                .produceType(ONION)
                .productSaleStatus(UNAVAILABLE)
                .farm(farm)
                .build();

        ProductMainImage productMainImage2 = ProductMainImage.builder()
                .mainImg("메인이미지2")
                .product(product2)
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

        FarmIntroductionInfo farmIntroductionInfo = FarmIntroductionInfo.builder()
                .mainImage("메인이미지")
                .introduction("한줄소개")
                .produceTypes(new ArrayList<>())
                .farm(farm)
                .build();

        FarmRepresentativeInfo farmRepresentativeInfo = FarmRepresentativeInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        productOptionList.add(productOption1);
        productOptionList.add(productOption2);

        when(mockProductRepository.findAllWithFarm()).thenReturn(productList);
        when(mockProductOptionRepository.findByProduct(productList.get(0))).thenReturn(productOptionList);
        when(mockProductOptionRepository.findByProduct(productList.get(1))).thenReturn(productOptionList);
        when(mockProductMainImageRepository.findByProduct(productList.get(0))).thenReturn(Optional.of(productMainImage1));
        when(mockProductMainImageRepository.findByProduct(productList.get(1))).thenReturn(Optional.of(productMainImage2));
        when(mockFarmBusinessInfoRepository.findByFarm(farm)).thenReturn(farmBusinessInfo);
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);
        when(mockFarmRepresentativeInfoRepository.findByFarm(farm)).thenReturn(farmRepresentativeInfo);

        List<ProductListResponseFormForUser> result = userProductService.getProductListForUser();

        assertEquals(result.get(0).getProductName(), "상품명1");
        assertEquals(result.get(0).getProductId(), 1L);
        assertEquals(result.get(0).getIsSoldOut(), false);
        assertEquals(result.get(0).getMinOptionPrice(), 13000L);
        assertEquals(result.get(0).getFarmName(), "투투농장");

        assertEquals(result.get(1).getProductName(), "상품명2");
        assertEquals(result.get(1).getProductId(), 2L);
        assertEquals(result.get(1).getIsSoldOut(), false);
        assertEquals(result.get(1).getMinOptionPrice(), 13000L);
        assertEquals(result.get(0).getFarmName(), "투투농장");
    }

    @Test
    @DisplayName("product mocking test: user product list by category")
    public void 사용자가_카테고리별_상품목록을_조회합니다() {
        List<Product> productList = new ArrayList<>();
        List<ProductOption> productOptionList = new ArrayList<>();

        Farm farm = Farm.builder()
                .farmName("투투농장")
                .build();

        FarmBusinessInfo farmBusinessInfo = FarmBusinessInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        Product product1 = Product.builder()
                .id(1L)
                .productName("상품명1")
                .productDescription("상품 설명1")
                .cultivationMethod(ORGANIC)
                .produceType(ONION)
                .productSaleStatus(AVAILABLE)
                .farm(farm)
                .build();

        ProductMainImage productMainImage1 = ProductMainImage.builder()
                .mainImg("메인이미지1")
                .product(product1)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("상품명2")
                .productDescription("상품 설명2")
                .cultivationMethod(ORGANIC)
                .produceType(ONION)
                .productSaleStatus(UNAVAILABLE)
                .farm(farm)
                .build();

        ProductMainImage productMainImage2 = ProductMainImage.builder()
                .mainImg("메인이미지2")
                .product(product2)
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

        FarmIntroductionInfo farmIntroductionInfo = FarmIntroductionInfo.builder()
                .mainImage("메인이미지")
                .introduction("한줄소개")
                .produceTypes(new ArrayList<>())
                .farm(farm)
                .build();

        FarmRepresentativeInfo farmRepresentativeInfo = FarmRepresentativeInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        when(mockProductRepository.findAllWithFarmByCategory(ORGANIC)).thenReturn(productList);
        when(mockProductOptionRepository.findByProduct(productList.get(0))).thenReturn(productOptionList);
        when(mockProductOptionRepository.findByProduct(productList.get(1))).thenReturn(productOptionList);
        when(mockFarmBusinessInfoRepository.findByFarm(farm)).thenReturn(farmBusinessInfo);
        when(mockProductMainImageRepository.findByProduct(productList.get(0))).thenReturn(Optional.of(productMainImage1));
        when(mockProductMainImageRepository.findByProduct(productList.get(1))).thenReturn(Optional.of(productMainImage2));
        when(mockFarmBusinessInfoRepository.findByFarm(farm)).thenReturn(farmBusinessInfo);
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);
        when(mockFarmRepresentativeInfoRepository.findByFarm(farm)).thenReturn(farmRepresentativeInfo);

        List<ProductListResponseFormForUser> result = userProductService.getProductListByCategoryForUser("ORGANIC");

        assertEquals(result.get(0).getProductName(), "상품명1");
        assertEquals(result.get(0).getProductId(), 1L);
        assertEquals(result.get(0).getIsSoldOut(), false);
        assertEquals(result.get(0).getMinOptionPrice(), 13000L);
        assertEquals(result.get(0).getFarmName(), "투투농장");

        assertEquals(result.get(1).getProductName(), "상품명2");
        assertEquals(result.get(1).getProductId(), 2L);
        assertEquals(result.get(1).getIsSoldOut(), false);
        assertEquals(result.get(1).getMinOptionPrice(), 13000L);
        assertEquals(result.get(0).getFarmName(), "투투농장");
    }

    @Test
    @DisplayName("product mocking test: user product list by region")
    public void 사용자가_농가_지역별_상품목록을_조회합니다() {
        final String region = "경기도";
        List<Product> productList = new ArrayList<>();
        List<FarmCustomerServiceInfo> farmList = new ArrayList<>();
        List<ProductOption> productOptionList = new ArrayList<>();

        Address address = new Address("경기도", null, null);
        Farm farm = Farm.builder()
                .farmName("투투농장")
                .build();

        FarmBusinessInfo farmBusinessInfo = FarmBusinessInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        FarmCustomerServiceInfo farmCustomerServiceInfo = FarmCustomerServiceInfo.builder()
                .id(farm.getId())
                .farmAddress(address)
                .farm(farm)
                .build();

        farmList.add(farmCustomerServiceInfo);

        Product product1 = Product.builder()
                .id(1L)
                .productName("상품명1")
                .productDescription("상품 설명1")
                .cultivationMethod(ORGANIC)
                .produceType(ONION)
                .productSaleStatus(AVAILABLE)
                .farm(farm)
                .build();

        ProductMainImage productMainImage1 = ProductMainImage.builder()
                .mainImg("메인이미지1")
                .product(product1)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("상품명2")
                .productDescription("상품 설명2")
                .cultivationMethod(ORGANIC)
                .produceType(ONION)
                .productSaleStatus(UNAVAILABLE)
                .farm(farm)
                .build();

        ProductMainImage productMainImage2 = ProductMainImage.builder()
                .mainImg("메인이미지2")
                .product(product2)
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

        FarmIntroductionInfo farmIntroductionInfo = FarmIntroductionInfo.builder()
                .mainImage("농가 메인이미지")
                .introduction("한줄소개")
                .produceTypes(new ArrayList<>())
                .farm(farm)
                .build();

        FarmRepresentativeInfo farmRepresentativeInfo = FarmRepresentativeInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        when(mockFarmCustomerServiceInfoRepository.findByFarmAddressAddressContaining(region)).thenReturn(farmList);
        when(mockProductRepository.findAllByFarmWithFarm(farm)).thenReturn(productList);
        when(mockProductOptionRepository.findByProduct(productList.get(0))).thenReturn(productOptionList);
        when(mockProductOptionRepository.findByProduct(productList.get(1))).thenReturn(productOptionList);
        when(mockProductMainImageRepository.findByProduct(productList.get(0))).thenReturn(Optional.of(productMainImage1));
        when(mockProductMainImageRepository.findByProduct(productList.get(1))).thenReturn(Optional.of(productMainImage2));
        when(mockFarmBusinessInfoRepository.findByFarm(farm)).thenReturn(farmBusinessInfo);
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);
        when(mockFarmRepresentativeInfoRepository.findByFarm(farm)).thenReturn(farmRepresentativeInfo);

        List<ProductListResponseFormForUser> result = userProductService.getProductListByRegionForUser(region);

        assertEquals(result.get(0).getProductName(), "상품명1");
        assertEquals(result.get(0).getProductId(), 1L);
        assertEquals(result.get(0).getIsSoldOut(), false);
        assertEquals(result.get(0).getMinOptionPrice(), 13000L);
        assertEquals(result.get(0).getFarmName(), "투투농장");
        assertEquals(result.get(0).getMainImage(), "농가 메인이미지");

        assertEquals(result.get(1).getProductName(), "상품명2");
        assertEquals(result.get(1).getProductId(), 2L);
        assertEquals(result.get(1).getIsSoldOut(), false);
        assertEquals(result.get(1).getMinOptionPrice(), 13000L);
        assertEquals(result.get(1).getFarmName(), "투투농장");
        assertEquals(result.get(1).getMainImage(), "농가 메인이미지");
    }
}

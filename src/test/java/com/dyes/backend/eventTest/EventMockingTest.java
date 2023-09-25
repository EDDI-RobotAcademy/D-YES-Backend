package com.dyes.backend.eventTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.event.controller.form.EventProductReadResponseForm;
import com.dyes.backend.domain.event.entity.EventDeadLine;
import com.dyes.backend.domain.event.entity.EventProduct;
import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import com.dyes.backend.domain.event.repository.EventDeadLineRepository;
import com.dyes.backend.domain.event.repository.EventOrderRepository;
import com.dyes.backend.domain.event.repository.EventProductRepository;
import com.dyes.backend.domain.event.repository.EventPurchaseCountRepository;
import com.dyes.backend.domain.event.service.EventServiceImpl;
import com.dyes.backend.domain.event.service.request.delete.EventProductDeleteRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyDeadLineRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.modify.ProductModifyUserTokenAndEventProductIdRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.EventProductAdminListResponse;
import com.dyes.backend.domain.event.service.response.EventProductListResponse;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmCustomerServiceInfo;
import com.dyes.backend.domain.farm.entity.FarmIntroductionInfo;
import com.dyes.backend.domain.farm.entity.FarmRepresentativeInfo;
import com.dyes.backend.domain.farm.repository.FarmCustomerServiceInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmIntroductionInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.repository.FarmRepresentativeInfoRepository;
import com.dyes.backend.domain.payment.service.PaymentService;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewRating;
import com.dyes.backend.domain.review.repository.ReviewRatingRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.user.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest

public class EventMockingTest {
    @Mock
    private EventProductRepository mockEventProductRepository;
    @Mock
    private EventPurchaseCountRepository mockEventPurchaseCountRepository;
    @Mock
    private EventDeadLineRepository mockEventDeadLineRepository;
    @Mock
    private AdminService mockAdminService;
    @Mock
    private FarmRepository mockFarmRepository;
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private ProductManagementRepository mockProductManagementRepository;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private ProductMainImageRepository mockProductMainImageRepository;
    @Mock
    private ProductDetailImagesRepository mockProductDetailImagesRepository;
    @Mock
    private FarmIntroductionInfoRepository mockFarmIntroductionInfoRepository;
    @Mock
    private FarmRepresentativeInfoRepository mockFarmRepresentativeInfoRepository;
    @Mock
    private ReviewRepository mockReviewRepository;
    @Mock
    private ReviewRatingRepository mockReviewRatingRepository;
    @Mock
    private FarmCustomerServiceInfoRepository mockFarmCustomerServiceInfoRepository;
    @Mock
    private EventOrderRepository mockEventOrderRepository;
    @Mock
    private PaymentService mockPaymentService;

    @InjectMocks
    private EventServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new EventServiceImpl(
                mockEventProductRepository,
                mockEventPurchaseCountRepository,
                mockEventDeadLineRepository,
                mockAdminService,
                mockFarmRepository,
                mockProductRepository,
                mockProductManagementRepository,
                mockProductOptionRepository,
                mockProductMainImageRepository,
                mockProductDetailImagesRepository,
                mockFarmIntroductionInfoRepository,
                mockFarmRepresentativeInfoRepository,
                mockReviewRepository,
                mockReviewRatingRepository,
                mockFarmCustomerServiceInfoRepository,
                mockEventOrderRepository,
                mockPaymentService
        );
    }
    @Test
    @DisplayName("event mocking test: evnet product register")
    public void 관리자가_공동구매_물품을_등록합니다() {
        final String detailImage = "상세이미지";

        EventProductRegisterRequest request = new EventProductRegisterRequest();
        EventProductRegisterDeadLineRequest deadLineRequest = new EventProductRegisterDeadLineRequest();
        EventProductRegisterPurchaseCountRequest countRequest = new EventProductRegisterPurchaseCountRequest();

        final String userToken = request.getUserToken();
        final String farmName = request.getFarmName();

        request.setDetailImgs(List.of(detailImage));

        Admin admin = new Admin();
        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(admin);
        Farm farm = new Farm();
        when(mockFarmRepository.findByFarmName(farmName)).thenReturn(Optional.of(farm));

        boolean result = mockService.eventProductRegister(request, deadLineRequest, countRequest);
        assertTrue(result);
    }
    @Test
    @DisplayName("event mocking test: evnet product list")
    public void 사용자가_공동구매_물품의_목록을_볼_수_있습니다() {
        Farm farm = new Farm();

        Product product = new Product();
        product.setFarm(farm);

        ProductOption productOption = new ProductOption();
        productOption.setProduct(product);
        productOption.setOptionSaleStatus(SaleStatus.AVAILABLE);

        EventDeadLine deadLine = new EventDeadLine();
        deadLine.setDeadLine(LocalDate.now());
        deadLine.setStartLine(LocalDate.now());
        EventPurchaseCount count = new EventPurchaseCount();
        count.setNowCount(1);
        count.setTargetCount(1);

        EventProduct eventProduct = new EventProduct();
        eventProduct.setProductOption(productOption);
        eventProduct.setEventDeadLine(deadLine);
        eventProduct.setEventPurchaseCount(count);

        when(mockEventProductRepository.findAllWithProductOptionDeadLineCount()).thenReturn(List.of(eventProduct));

        FarmIntroductionInfo farmIntroductionInfo = new FarmIntroductionInfo();
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);
        FarmRepresentativeInfo farmRepresentativeInfo = new FarmRepresentativeInfo();
        when(mockFarmRepresentativeInfoRepository.findByFarm(farm)).thenReturn(farmRepresentativeInfo);

        Review review = new Review();
        when(mockReviewRepository.findAllByProduct(product)).thenReturn(List.of(review));
        ReviewRating reviewRating = new ReviewRating();
        when(mockReviewRatingRepository.findByReview(review)).thenReturn(Optional.of(reviewRating));

        ProductMainImage mainImage = new ProductMainImage();
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(mainImage));

        List<EventProductListResponse> result = mockService.eventProductList();
        assertTrue(result != null);
    }
    @Test
    @DisplayName("event mocking test: evnet product read")
    public void 사용자가_공동구매_물품을_볼_수_있습니다() {
        final Long eventProductId = 1L;

        Farm farm = new Farm();
        farm.setFarmName("1");

        Product product = new Product();
        product.setId(1L);
        product.setProductName("1");
        product.setProductDescription("1");
        product.setCultivationMethod(CultivationMethod.ENVIRONMENT_FRIENDLY);
        product.setFarm(farm);

        ProductOption productOption = new ProductOption();
        productOption.setId(1L);
        productOption.setOptionName("1");
        productOption.setOptionPrice(1L);
        productOption.setStock(1);
        productOption.setAmount(new Amount(1L, Unit.EA));
        productOption.setProduct(product);
        productOption.setOptionSaleStatus(SaleStatus.AVAILABLE);

        EventDeadLine deadLine = new EventDeadLine();
        deadLine.setDeadLine(LocalDate.now());
        deadLine.setStartLine(LocalDate.now());
        EventPurchaseCount count = new EventPurchaseCount();
        count.setNowCount(1);
        count.setTargetCount(1);

        EventProduct eventProduct = new EventProduct();
        eventProduct.setProductOption(productOption);
        eventProduct.setEventDeadLine(deadLine);
        eventProduct.setEventPurchaseCount(count);

        when(mockEventProductRepository.findByIdProductOptionDeadLineCount(eventProductId)).thenReturn(Optional.of(eventProduct));

        FarmIntroductionInfo farmIntroductionInfo = new FarmIntroductionInfo();
        farmIntroductionInfo.setFarm(farm);
        farmIntroductionInfo.setMainImage("1");
        farmIntroductionInfo.setIntroduction("1");
        farmIntroductionInfo.setProduceTypes(new ArrayList<>());
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);
        FarmCustomerServiceInfo farmCustomerServiceInfo = new FarmCustomerServiceInfo();
        farmCustomerServiceInfo.setFarm(farm);
        farmCustomerServiceInfo.setCsContactNumber("1");
        farmCustomerServiceInfo.setFarmAddress(new Address());
        when(mockFarmCustomerServiceInfoRepository.findByFarm(farm)).thenReturn(farmCustomerServiceInfo);

        Review review = new Review();
        when(mockReviewRepository.findAllByProduct(product)).thenReturn(List.of(review));
        ReviewRating reviewRating = new ReviewRating();
        reviewRating.setReview(review);
        when(mockReviewRatingRepository.findByReview(review)).thenReturn(Optional.of(reviewRating));

        ProductMainImage mainImage = new ProductMainImage();
        mainImage.setProduct(product);
        mainImage.setId(1L);
        mainImage.setMainImg("1");
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(mainImage));

        ProductDetailImages detailImages = new ProductDetailImages();
        detailImages.setProduct(product);
        detailImages.setId(1L);
        detailImages.setDetailImgs("1");
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(List.of(detailImages));

        EventProductReadResponseForm result = mockService.eventProductRead(eventProductId);

        assertTrue(result != null);
    }
    @Test
    @DisplayName("event mocking test: evnet product modify")
    public void 관리자가_공동구매_물품의_내용을_수정할_수_있습니다() {
        final String userToken = "유저 토큰";
        final Long eventProductId = 1L;
        ProductModifyUserTokenAndEventProductIdRequest productModifyUserTokenAndEventProductIdRequest = new ProductModifyUserTokenAndEventProductIdRequest();
        productModifyUserTokenAndEventProductIdRequest.setUserToken(userToken);
        productModifyUserTokenAndEventProductIdRequest.setEventProductId(eventProductId);
        ProductModifyRequest productModifyRequest = new ProductModifyRequest();
        ProductOptionModifyRequest productOptionModifyRequest = new ProductOptionModifyRequest();
        ProductMainImageModifyRequest productMainImageModifyRequest = new ProductMainImageModifyRequest();
        List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequest = new ArrayList<>();
        EventProductModifyDeadLineRequest eventProductModifyDeadLineRequest = new EventProductModifyDeadLineRequest();
        EventProductModifyPurchaseCountRequest eventProductModifyPurchaseCountRequest = new EventProductModifyPurchaseCountRequest();

        Admin admin = new Admin();
        when(mockAdminService.findAdminByUserToken(productModifyUserTokenAndEventProductIdRequest.getUserToken())).thenReturn(admin);

        Farm farm = new Farm();

        Product product = new Product();
        product.setFarm(farm);

        ProductOption productOption = new ProductOption();
        productOption.setProduct(product);
        productOption.setOptionSaleStatus(SaleStatus.AVAILABLE);

        EventDeadLine deadLine = new EventDeadLine();
        deadLine.setDeadLine(LocalDate.now());
        deadLine.setStartLine(LocalDate.now());
        EventPurchaseCount count = new EventPurchaseCount();
        count.setNowCount(1);
        count.setTargetCount(1);

        EventProduct eventProduct = new EventProduct();
        eventProduct.setProductOption(productOption);
        eventProduct.setEventDeadLine(deadLine);
        eventProduct.setEventPurchaseCount(count);
        when(mockEventProductRepository.findByIdProductOptionDeadLineCount(productModifyUserTokenAndEventProductIdRequest.getEventProductId())).thenReturn(Optional.of(eventProduct));

        ProductMainImage mainImage = new ProductMainImage();
        mainImage.setProduct(product);
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(mainImage));

        ProductDetailImages detailImages = new ProductDetailImages();
        detailImages.setProduct(product);
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(List.of(detailImages));

        boolean result = mockService.eventProductModify(
                productModifyUserTokenAndEventProductIdRequest, productModifyRequest, productOptionModifyRequest,
                productMainImageModifyRequest, productDetailImagesModifyRequest,
                eventProductModifyDeadLineRequest, eventProductModifyPurchaseCountRequest
                );
        assertTrue(result);
    }
    @Test
    @DisplayName("event mocking test: evnet product delete")
    public void 사용자는_공동구매_물품을_삭제_할_수_있습니다() {
        final String userToken = "유저 토큰";
        final Long eventProductId = 1L;

        EventProductDeleteRequest deleteRequest = new EventProductDeleteRequest(userToken, eventProductId);

        Admin admin = new Admin();
        when(mockAdminService.findAdminByUserToken(deleteRequest.getUserToken())).thenReturn(admin);
        Farm farm = new Farm();

        Product product = new Product();
        product.setFarm(farm);

        ProductOption productOption = new ProductOption();
        productOption.setProduct(product);
        productOption.setOptionSaleStatus(SaleStatus.AVAILABLE);

        EventDeadLine deadLine = new EventDeadLine();
        deadLine.setDeadLine(LocalDate.now());
        deadLine.setStartLine(LocalDate.now());
        EventPurchaseCount count = new EventPurchaseCount();
        count.setNowCount(1);
        count.setTargetCount(1);

        EventProduct eventProduct = new EventProduct();
        eventProduct.setProductOption(productOption);
        eventProduct.setEventDeadLine(deadLine);
        eventProduct.setEventPurchaseCount(count);
        when(mockEventProductRepository.findByIdProductOptionDeadLineCount(deleteRequest.getEventProductId())).thenReturn(Optional.of(eventProduct));

        ProductMainImage mainImage = new ProductMainImage();
        mainImage.setProduct(product);
        when(mockProductMainImageRepository.findByProduct(product)).thenReturn(Optional.of(mainImage));

        ProductDetailImages detailImages = new ProductDetailImages();
        detailImages.setProduct(product);
        when(mockProductDetailImagesRepository.findByProduct(product)).thenReturn(List.of(detailImages));

        boolean result = mockService.eventProductDelete(deleteRequest);
        assertTrue(result);
    }
    @Test
    @DisplayName("event mocking test: admin evnet product list")
    public void 관리자는_이벤트_상품_현황_목록을_조회_할_수_있습니다() {
        Farm farm = new Farm();

        Product product = new Product();
        product.setFarm(farm);

        ProductOption productOption = new ProductOption();
        productOption.setProduct(product);
        productOption.setOptionSaleStatus(SaleStatus.AVAILABLE);

        EventDeadLine deadLine = new EventDeadLine();
        deadLine.setDeadLine(LocalDate.now());
        deadLine.setStartLine(LocalDate.now());
        EventPurchaseCount count = new EventPurchaseCount();
        count.setNowCount(1);
        count.setTargetCount(1);

        EventProduct eventProduct = new EventProduct();
        eventProduct.setProductOption(productOption);
        eventProduct.setEventDeadLine(deadLine);
        eventProduct.setEventPurchaseCount(count);

        when(mockEventProductRepository.findAllWithProductOptionDeadLineCount()).thenReturn(List.of(eventProduct));

        List<EventProductAdminListResponse> result = mockService.eventProductAdminList();
        assertTrue(result != null);
    }
}

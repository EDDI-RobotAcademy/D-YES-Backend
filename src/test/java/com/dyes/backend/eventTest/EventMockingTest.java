package com.dyes.backend.eventTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.event.entity.EventDeadLine;
import com.dyes.backend.domain.event.entity.EventProduct;
import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import com.dyes.backend.domain.event.repository.EventDeadLineRepository;
import com.dyes.backend.domain.event.repository.EventProductRepository;
import com.dyes.backend.domain.event.repository.EventPurchaseCountRepository;
import com.dyes.backend.domain.event.service.EventServiceImpl;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.EventProductListResponse;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmIntroductionInfo;
import com.dyes.backend.domain.farm.entity.FarmRepresentativeInfo;
import com.dyes.backend.domain.farm.repository.FarmIntroductionInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.repository.FarmRepresentativeInfoRepository;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductMainImage;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.entity.SaleStatus;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewRating;
import com.dyes.backend.domain.review.repository.ReviewRatingRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
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
                mockReviewRatingRepository
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
}

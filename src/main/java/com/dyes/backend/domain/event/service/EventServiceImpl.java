package com.dyes.backend.domain.event.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.event.entity.EventDeadLine;
import com.dyes.backend.domain.event.entity.EventProduct;
import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import com.dyes.backend.domain.event.repository.EventDeadLineRepository;
import com.dyes.backend.domain.event.repository.EventProductRepository;
import com.dyes.backend.domain.event.repository.EventPurchaseCountRepository;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.EventProductDeadLineResponse;
import com.dyes.backend.domain.event.service.response.EventProductListResponse;
import com.dyes.backend.domain.event.service.response.EventProductPurchaseCountResponse;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmIntroductionInfo;
import com.dyes.backend.domain.farm.entity.FarmRepresentativeInfo;
import com.dyes.backend.domain.farm.repository.FarmIntroductionInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.repository.FarmRepresentativeInfoRepository;
import com.dyes.backend.domain.farm.service.request.FarmAuthenticationRequest;
import com.dyes.backend.domain.product.controller.admin.form.ProductRegisterRequestForm;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.admin.request.register.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductOptionRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductRegisterRequest;
import com.dyes.backend.domain.product.service.user.response.FarmInfoResponseForListForUser;
import com.dyes.backend.domain.product.service.user.response.ProductMainImageResponseForListForUser;
import com.dyes.backend.domain.product.service.user.response.ProductOptionResponseForListForUser;
import com.dyes.backend.domain.product.service.user.response.ProductResponseForListForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReviewResponseForUser;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewRating;
import com.dyes.backend.domain.review.repository.ReviewRatingRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.dyes.backend.domain.product.entity.SaleStatus.AVAILABLE;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    final private EventProductRepository eventProductRepository;
    final private EventPurchaseCountRepository eventPurchaseCountRepository;
    final private EventDeadLineRepository eventDeadLineRepository;
    final private AdminService adminService;
    final private FarmRepository farmRepository;
    final private ProductRepository productRepository;
    final private ProductManagementRepository productManagementRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private ProductDetailImagesRepository productDetailImagesRepository;
    final private FarmIntroductionInfoRepository farmIntroductionInfoRepository;
    final private FarmRepresentativeInfoRepository farmRepresentativeInfoRepository;
    final private ReviewRepository reviewRepository;
    final private ReviewRatingRepository reviewRatingRepository;

    public boolean eventProductRegister(EventProductRegisterRequest productRequest,
                                        EventProductRegisterDeadLineRequest deadLineRequest,
                                        EventProductRegisterPurchaseCountRequest countRequest){
        ProductOption productOption = saveEventProduct(productRequest);
        try {
            if (productOption == null){
                return false;
            }
            EventDeadLine deadLine = EventDeadLine.builder()
                    .startLine(deadLineRequest.getStartLine())
                    .deadLine(deadLineRequest.getDeadLine())
                    .build();
            eventDeadLineRepository.save(deadLine);

            EventPurchaseCount count = EventPurchaseCount.builder()
                    .targetCount(countRequest.getTargetCount())
                    .build();
            eventPurchaseCountRepository.save(count);

            EventProduct eventProduct = EventProduct.builder()
                    .productOption(productOption)
                    .eventDeadLine(deadLine)
                    .eventPurchaseCount(count)
                    .build();

            eventProductRepository.save(eventProduct);
            return true;
        } catch (Exception e) {
            log.error("Failed to register the product: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<EventProductListResponse> eventProductList() {
        try {
            List<EventProduct> eventProductList = eventProductRepository.findAllWithProductOptionDeadLineCount();

            List<EventProductListResponse> responseList = new ArrayList<>();
            for (EventProduct eventProduct : eventProductList) {
                if (eventProductList.size() != 0){
                    // 찾아온 EventProduct의 파츠들을 정의해주기
                    Product product = eventProduct.getProductOption().getProduct();
                    ProductOption productOption = eventProduct.getProductOption();
                    Farm farm = eventProduct.getProductOption().getProduct().getFarm();
                    FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(farm);
                    FarmRepresentativeInfo farmRepresentativeInfo = farmRepresentativeInfoRepository.findByFarm(farm);
                    EventDeadLine deadLine = eventProduct.getEventDeadLine();
                    EventPurchaseCount count = eventProduct.getEventPurchaseCount();
                    List<Review> reviewList = reviewRepository.findAllByProduct(product);

                    Integer reviewCount = 0;
                    Integer sumRating = 0;

                    if (reviewList.size() == 0) {
                        reviewCount = 1;
                    }

                    for (Review review : reviewList) {
                        Optional<ReviewRating> maybeRating = reviewRatingRepository.findByReview(review);
                        if (maybeRating.isEmpty()) {
                            sumRating += maybeRating.get().getRating();
                        }
                    }
                    boolean isSoldOut = false;
                    if (productOption.getOptionSaleStatus().equals(AVAILABLE)){
                        isSoldOut = true;
                    }

                    Optional<ProductMainImage> maybeMainImage = productMainImageRepository.findByProduct(product);
                    if (maybeMainImage.isEmpty()){
                        return null;
                    }
                    ProductMainImage mainImage = maybeMainImage.get();

                    // DTO에 담기
                    ProductResponseForListForUser productResponse = new ProductResponseForListForUser(
                            product.getId(), product.getProductName(), product.getCultivationMethod()
                    );
                    ProductMainImageResponseForListForUser mainImageResponse = new ProductMainImageResponseForListForUser(
                            mainImage.getMainImg()
                    );

                    ProductOptionResponseForListForUser productOptionResponse = new ProductOptionResponseForListForUser(
                            productOption.getOptionPrice(), isSoldOut
                    );
                    FarmInfoResponseForListForUser farmInfoResponse = new FarmInfoResponseForListForUser(
                            farm.getFarmName(), farmIntroductionInfo.getMainImage(), farmRepresentativeInfo.getRepresentativeName()
                    );
                    ProductReviewResponseForUser reviewResponse = new ProductReviewResponseForUser(
                            reviewCount, sumRating/reviewCount
                    );
                    EventProductDeadLineResponse deadLineResponse = new EventProductDeadLineResponse(deadLine.getStartLine(), deadLine.getDeadLine());
                    EventProductPurchaseCountResponse countResponse = new EventProductPurchaseCountResponse(count.getTargetCount(), count.getNowCount());


                    EventProductListResponse response = EventProductListResponse.builder()
                            .productResponseForListForUser(productResponse)
                            .productMainImageResponseForListForUser(mainImageResponse)
                            .productOptionResponseForListForUser(productOptionResponse)
                            .farmInfoResponseForListForUser(farmInfoResponse)
                            .productReviewResponseForUser(reviewResponse)
                            .countResponse(countResponse)
                            .deadLineResponse(deadLineResponse)
                            .build();
                    responseList.add(response);
                }
            }
            return responseList;
        } catch (Exception e) {
            log.error("Failed to list the product: {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductOption saveEventProduct(EventProductRegisterRequest productRequest) {
        log.info("Registering a new product");

        final String userToken = productRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return null;
        }

        final String farmName = productRequest.getFarmName();

        Optional<Farm> maybeFarm = farmRepository.findByFarmName(farmName);
        if (maybeFarm.isEmpty()) {
            log.info("Farm with name '{}' not found", farmName);
            return null;
        }

        Farm farm = maybeFarm.get();

        try {
            Product product = Product.builder()
                    .productName(productRequest.getProductName())
                    .productDescription(productRequest.getProductDescription())
                    .cultivationMethod(productRequest.getCultivationMethod())
                    .produceType(productRequest.getProduceType())
                    .productSaleStatus(AVAILABLE)
                    .farm(farm)
                    .build();

            productRepository.save(product);

            ProductManagement productManagement = ProductManagement.builder()
                    .id(product.getId())
                    .createdDate(LocalDate.now())
                    .adminId(admin.getId())
                    .product(product)
                    .build();

            productManagementRepository.save(productManagement);

                ProductOption productOption = ProductOption.builder()
                        .optionPrice(productRequest.getOptionPrice())
                        .stock(productRequest.getStock())
                        .optionName(productRequest.getOptionName())
                        .amount(Amount.builder()
                                .value(productRequest.getValue())
                                .unit(productRequest.getUnit())
                                .build())
                        .product(product)
                        .optionSaleStatus(AVAILABLE)
                        .build();

                productOptionRepository.save(productOption);

            ProductMainImage mainImage = ProductMainImage.builder()
                    .id(product.getId())
                    .mainImg(productRequest.getMainImg())
                    .product(product)
                    .build();

            productMainImageRepository.save(mainImage);

            for (String detailImage : productRequest.getDetailImgs()) {
                ProductDetailImages detailImages = ProductDetailImages.builder()
                        .detailImgs(detailImage)
                        .product(product)
                        .build();

                productDetailImagesRepository.save(detailImages);
            }

            log.info("Product registration successful");
            return productOption;

        } catch (Exception e) {
            log.error("Failed to register the product: {}", e.getMessage(), e);
            return null;
        }
    }
}

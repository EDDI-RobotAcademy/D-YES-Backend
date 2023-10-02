package com.dyes.backend.domain.event.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.event.controller.form.EventProductModifyRequestForm;
import com.dyes.backend.domain.event.controller.form.EventProductReadResponseForm;
import com.dyes.backend.domain.event.entity.EventDeadLine;
import com.dyes.backend.domain.event.entity.EventOrder;
import com.dyes.backend.domain.event.entity.EventProduct;
import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import com.dyes.backend.domain.event.repository.EventDeadLineRepository;
import com.dyes.backend.domain.event.repository.EventOrderRepository;
import com.dyes.backend.domain.event.repository.EventProductRepository;
import com.dyes.backend.domain.event.repository.EventPurchaseCountRepository;
import com.dyes.backend.domain.event.service.request.delete.EventProductDeleteRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyDeadLineRequest;
import com.dyes.backend.domain.event.service.request.modify.EventProductModifyPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.modify.ProductModifyUserTokenAndEventProductIdRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.*;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmCustomerServiceInfo;
import com.dyes.backend.domain.farm.entity.FarmIntroductionInfo;
import com.dyes.backend.domain.farm.entity.FarmRepresentativeInfo;
import com.dyes.backend.domain.farm.repository.FarmCustomerServiceInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmIntroductionInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.repository.FarmRepresentativeInfoRepository;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForUser;
import com.dyes.backend.domain.payment.service.PaymentService;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;
import com.dyes.backend.domain.product.service.user.response.*;
import com.dyes.backend.domain.product.service.user.response.form.ProductReviewResponseForUser;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewRating;
import com.dyes.backend.domain.review.repository.ReviewRatingRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dyes.backend.domain.product.entity.MaybeEventProduct.YES;
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
    final private FarmCustomerServiceInfoRepository farmCustomerServiceInfoRepository;
    final private EventOrderRepository eventOrderRepository;
    final private PaymentService paymentService;

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
                    .nowCount(0)
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
                    Integer averageRating = 0;

                    for (Review review : reviewList) {
                        Optional<ReviewRating> maybeRating = reviewRatingRepository.findByReview(review);
                        if (maybeRating.isEmpty()) {
                            sumRating += maybeRating.get().getRating();
                        }
                    }

                    if (reviewList.size() != 0) {
                        reviewCount = reviewList.size();
                        averageRating = sumRating/reviewCount;
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
                            reviewCount, averageRating
                    );
                    EventProductDeadLineResponse deadLineResponse = new EventProductDeadLineResponse(deadLine.getStartLine(), deadLine.getDeadLine());
                    EventProductPurchaseCountResponse countResponse = new EventProductPurchaseCountResponse(count.getTargetCount(), count.getNowCount());
                    EventProductIdResponse eventProductIdResponse = new EventProductIdResponse(eventProduct.getId());


                    EventProductListResponse response = EventProductListResponse.builder()
                            .productResponseForListForUser(productResponse)
                            .productMainImageResponseForListForUser(mainImageResponse)
                            .productOptionResponseForListForUser(productOptionResponse)
                            .farmInfoResponseForListForUser(farmInfoResponse)
                            .productReviewResponseForUser(reviewResponse)
                            .countResponse(countResponse)
                            .deadLineResponse(deadLineResponse)
                            .eventProductIdResponse(eventProductIdResponse)
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
    public EventProductReadResponseForm eventProductRead(Long eventProductId) {
        try {
            Optional<EventProduct> maybeEventProduct = eventProductRepository.findByIdProductOptionDeadLineCount(eventProductId);
            if (maybeEventProduct.isEmpty()) {
                return null;
            }
            EventProduct eventProduct = maybeEventProduct.get();

            Product product = eventProduct.getProductOption().getProduct();
            ProductOption productOption = eventProduct.getProductOption();
            Farm farm = eventProduct.getProductOption().getProduct().getFarm();
            FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(farm);
            FarmCustomerServiceInfo farmCustomerServiceInfo = farmCustomerServiceInfoRepository.findByFarm(farm);

            EventDeadLine deadLine = eventProduct.getEventDeadLine();
            EventPurchaseCount count = eventProduct.getEventPurchaseCount();
            List<Review> reviewList = reviewRepository.findAllByProduct(product);

            Integer reviewCount = 0;
            Integer sumRating = 0;
            Integer averageRating = 0;

            for (Review review : reviewList) {
                Optional<ReviewRating> maybeRating = reviewRatingRepository.findByReview(review);
                if (maybeRating.isEmpty()) {
                    sumRating += maybeRating.get().getRating();
                }
            }

            if (reviewList.size() != 0) {
                reviewCount = reviewList.size();
                averageRating = sumRating/reviewCount;
            }

            Optional<ProductMainImage> maybeMainImage = productMainImageRepository.findByProduct(product);
            if (maybeMainImage.isEmpty()){
                return null;
            }
            ProductMainImage mainImage = maybeMainImage.get();

            List<ProductDetailImages> productDetailImagesList = productDetailImagesRepository.findByProduct(product);

            ProductResponseForUser productResponseForUser = new ProductResponseForUser(
                    product.getId(), product.getProductName(), product.getProductDescription(), product.getCultivationMethod()
            );
            ProductOptionResponseForUser productOptionResponseForUser = new ProductOptionResponseForUser(
                    productOption.getId(),productOption.getOptionName(), productOption.getOptionPrice(), productOption.getOptionSaleStatus(),
                    productOption.getStock(), productOption.getAmount().getValue(), productOption.getAmount().getUnit()
            );
            ProductMainImageResponseForUser productMainImageResponseForUser = new ProductMainImageResponseForUser(
                    mainImage.getId(), mainImage.getMainImg()
            );
            List<ProductDetailImagesResponseForUser> detailImagesResponseForUserList = new ArrayList<>();
            for (ProductDetailImages detailImages : productDetailImagesList){
                ProductDetailImagesResponseForUser productDetailImagesResponseForUser = new ProductDetailImagesResponseForUser(
                        detailImages.getId(), detailImages.getDetailImgs()
                );
                detailImagesResponseForUserList.add(productDetailImagesResponseForUser);
            }
            FarmInfoResponseForUser farmInfoResponseForUser = new FarmInfoResponseForUser(
                    farm.getFarmName(), farmCustomerServiceInfo.getCsContactNumber(), farmCustomerServiceInfo.getFarmAddress(),
                    farmIntroductionInfo.getMainImage(), farmIntroductionInfo.getIntroduction(), farmIntroductionInfo.getProduceTypes()
            );
            ProductReviewResponseForUser productReviewResponseForUser = new ProductReviewResponseForUser(
                    reviewCount, averageRating
            );
            EventProductDeadLineResponse deadLineResponse = new EventProductDeadLineResponse(
                    deadLine.getStartLine(), deadLine.getDeadLine()
            );
            EventProductPurchaseCountResponse countResponse = new EventProductPurchaseCountResponse(
                    count.getTargetCount(), count.getNowCount()
            );
            EventProductProduceTypeResponse produceTypeResponse = new EventProductProduceTypeResponse(product.getProduceType());

            EventProductReadResponseForm responseForm = EventProductReadResponseForm.builder()
                    .productResponseForUser(productResponseForUser)
                    .optionResponseForUser(productOptionResponseForUser)
                    .mainImageResponseForUser(productMainImageResponseForUser)
                    .detailImagesForUser(detailImagesResponseForUserList)
                    .farmInfoResponseForUser(farmInfoResponseForUser)
                    .productReviewResponseForUser(productReviewResponseForUser)
                    .eventProductDeadLineResponse(deadLineResponse)
                    .eventProductPurchaseCountResponse(countResponse)
                    .eventProductProduceTypeResponse(produceTypeResponse)
                    .build();

            return responseForm;
        } catch (Exception e) {
            log.error("Failed to list the product: {}", e.getMessage(), e);
            return null;
        }
    }
    // 관리자가 공동 구매 물품 내용을 수정합니다
    public boolean eventProductModify(Long eventProductId,ProductModifyUserTokenAndEventProductIdRequest productModifyUserTokenAndEventProductIdRequest,
                                      ProductModifyRequest productModifyRequest, ProductOptionModifyRequest productOptionModifyRequest,
                                      ProductMainImageModifyRequest productMainImageModifyRequest, List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequest,
                                      EventProductModifyDeadLineRequest eventProductModifyDeadLineRequest, EventProductModifyPurchaseCountRequest eventProductModifyPurchaseCountRequest) {

        final String userToken = productModifyUserTokenAndEventProductIdRequest.getUserToken();


        try {
            final Admin admin = adminService.findAdminByUserToken(userToken);

            if (admin == null) {
                log.info("Unable to find admin with user token: {}", userToken);
                return false;
            }

            Optional<EventProduct> maybeEventProduct = eventProductRepository.findByIdProductOptionDeadLineCount(eventProductId);
            if (maybeEventProduct.isEmpty()){
                log.info("maybeEventProduct: " + maybeEventProduct.get().getId());
                return false;
            }
            EventProduct eventProduct = maybeEventProduct.get();
            // 상품 수정 진행
            Product product = eventProduct.getProductOption().getProduct();

            product.setProductName(productModifyRequest.getProductName());
            product.setProductDescription(productModifyRequest.getProductDescription());
            product.setCultivationMethod(productModifyRequest.getCultivationMethod());
            productRepository.save(product);

            Optional<ProductMainImage> maybeMainImage = productMainImageRepository.findByProduct(product);
            if (maybeMainImage.isPresent()){
                ProductMainImage mainImage = maybeMainImage.get();

                mainImage.setMainImg(productMainImageModifyRequest.getMainImg());
                mainImage.setProduct(product);
                productMainImageRepository.save(mainImage);
            } else {
                ProductMainImage mainImage = ProductMainImage.builder()
                        .mainImg(productMainImageModifyRequest.getMainImg())
                        .product(product)
                        .build();

                productMainImageRepository.save(mainImage);
            }

            // DB에 저장된 상품 상세 이미지 가져오기
            List<ProductDetailImages> productDetailImagesList = productDetailImagesRepository.findByProductWithProduct(product);
            if (productDetailImagesList.size() != 0) {

                Set<Long> requestImages = productDetailImagesModifyRequest.stream()
                        .map(ProductDetailImagesModifyRequest :: getDetailImageId)
                        .collect(Collectors.toSet());

                for (ProductDetailImagesModifyRequest detailImagesModifyRequest : productDetailImagesModifyRequest) {
                    if (detailImagesModifyRequest.getDetailImageId() == 0) {
                        ProductDetailImages detailImages = ProductDetailImages.builder()
                                .detailImgs(detailImagesModifyRequest.getDetailImgs())
                                .product(product)
                                .build();
                        productDetailImagesRepository.save(detailImages);
                    }
                }
                List<ProductDetailImages> deleteImages = new ArrayList<>();
                for (ProductDetailImages productDetailImages : productDetailImagesList) {
                    if (!requestImages.contains(productDetailImages.getId())) {
                        deleteImages.add(productDetailImages);
                    }
                }
                for (ProductDetailImages deleteImage : deleteImages) {
                    productDetailImagesRepository.delete(deleteImage);
                }
            } else {
                for (ProductDetailImagesModifyRequest detailImagesModifyRequest : productDetailImagesModifyRequest) {
                    ProductDetailImages detailImages = ProductDetailImages.builder()
                            .detailImgs(detailImagesModifyRequest.getDetailImgs())
                            .product(product)
                            .build();
                    productDetailImagesRepository.save(detailImages);
                }
            }


            ProductOption productOption = eventProduct.getProductOption();

            productOption.setOptionName(productOptionModifyRequest.getOptionName());
            productOption.setOptionPrice(productOptionModifyRequest.getOptionPrice());
            productOption.setStock(productOptionModifyRequest.getStock());
            productOption.setAmount(new Amount(productOptionModifyRequest.getValue(), productOptionModifyRequest.getUnit()));
            productOption.setProduct(product);
            productOptionRepository.save(productOption);

            EventDeadLine eventDeadLine = eventProduct.getEventDeadLine();
            eventDeadLine.setStartLine(eventProductModifyDeadLineRequest.getStartLine());
            eventDeadLine.setDeadLine(eventProductModifyDeadLineRequest.getDeadLine());
            eventDeadLineRepository.save(eventDeadLine);

            EventPurchaseCount count = eventProduct.getEventPurchaseCount();
            count.setTargetCount(eventProductModifyPurchaseCountRequest.getTargetCount());
            eventPurchaseCountRepository.save(count);

            return true;

        } catch (Exception e) {
            log.error("Failed to modify the product: {}", e.getMessage(), e);
            return false;
        }

    }
    public boolean eventProductDelete(Long eventProductId, String userToken) {
        try {
            Admin admin = adminService.findAdminByUserToken(userToken);

            if (admin == null) {
                log.info("Unable to find admin with user token: {}", userToken);
                return false;
            }

            Optional<EventProduct> maybeEventProduct = eventProductRepository.findByIdProductOptionDeadLineCount(eventProductId);
            if (maybeEventProduct.isEmpty()){
                log.info("There are no matching event product");
                return false;
            }

            EventProduct eventProduct = maybeEventProduct.get();
            Product product = eventProduct.getProductOption().getProduct();
            ProductOption productOption = eventProduct.getProductOption();
            EventPurchaseCount count = eventProduct.getEventPurchaseCount();
            EventDeadLine deadLine = eventProduct.getEventDeadLine();

            if (LocalDate.now().isAfter(deadLine.getStartLine())) {
                log.info("Cannot be deleted before event closes");
                return false;
            }

            Optional<ProductMainImage> maybeMainImage = productMainImageRepository.findByProduct(product);
            if (maybeMainImage.isPresent()){
                ProductMainImage mainImage = maybeMainImage.get();
                productMainImageRepository.delete(mainImage);
            }


            List<ProductDetailImages> productDetailImagesList = productDetailImagesRepository.findByProductWithProduct(product);
            for (ProductDetailImages productDetailImages : productDetailImagesList) {
                productDetailImagesRepository.delete(productDetailImages);
            }

            List<Review> reviewList = reviewRepository.findAllByProduct(product);
            for (Review review : reviewList) {
                Optional<ReviewRating> maybeRating = reviewRatingRepository.findByReview(review);
                if (maybeRating.isPresent()) {
                    reviewRatingRepository.delete(maybeRating.get());
                }
                reviewRepository.delete(review);
            }

            Optional<ProductManagement> maybeProductManagement = productManagementRepository.findByProduct(product);
            if (maybeProductManagement.isPresent()){
                productManagementRepository.delete(maybeProductManagement.get());
            }

            eventProductRepository.delete(eventProduct);

            eventDeadLineRepository.delete(deadLine);
            eventPurchaseCountRepository.delete(count);

            productOptionRepository.delete(productOption);
            productRepository.delete(product);

            return true;
        } catch (Exception e) {
            log.error("Failed to delete the product: {}", e.getMessage(), e);
            return false;
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
                    .maybeEventProduct(YES)
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

    // 관리자의 이벤트 상품 현황 목록 보기
    public List<EventProductAdminListResponse> eventProductAdminList() {
        List<EventProduct> eventProductList = eventProductRepository.findAllWithProductOptionDeadLineCount();
        List<EventProductAdminListResponse> responseList = new ArrayList<>();

        for (EventProduct eventProduct : eventProductList) {
            if (eventProduct.getEventPurchaseCount().getNowCount() < eventProduct.getEventPurchaseCount().getTargetCount()) {
                EventProductAdminListResponse response = EventProductAdminListResponse.builder()
                        .eventProductId(eventProduct.getId())
                        .eventProductName(eventProduct.getProductOption().getProduct().getProductName())
                        .stock(eventProduct.getProductOption().getStock())
                        .deadLine(eventProduct.getEventDeadLine().getDeadLine())
                        .startLine(eventProduct.getEventDeadLine().getStartLine())
                        .discountRate(30 * (eventProduct.getEventPurchaseCount().getNowCount())/(eventProduct.getEventPurchaseCount().getTargetCount()))
                        .eventPurchaseCount(eventProduct.getEventPurchaseCount().getNowCount())
                        .build();
                responseList.add(response);
            } else {
                EventProductAdminListResponse response = EventProductAdminListResponse.builder()
                        .eventProductId(eventProduct.getId())
                        .eventProductName(eventProduct.getProductOption().getProduct().getProductName())
                        .stock(eventProduct.getProductOption().getStock())
                        .deadLine(eventProduct.getEventDeadLine().getDeadLine())
                        .startLine(eventProduct.getEventDeadLine().getStartLine())
                        .discountRate(30)
                        .eventPurchaseCount(eventProduct.getEventPurchaseCount().getNowCount())
                        .build();
                responseList.add(response);
            }
        }
        return responseList;
    }
    // 이벤트 상품 페이백 해주기
    @Scheduled(cron = "0 1 0 * * ?")
    public void eventProductRefund() {
        List<EventOrder> orderList = eventOrderRepository.findAllWithDeadlineAndCountAndUser();
        for (EventOrder eventOrder : orderList) {
            if (LocalDate.now().equals(eventOrder.getEventProduct().getEventDeadLine().getDeadLine())){
                paymentService.paymentEventProductRefundRequest(eventOrder);
            }
        }
    }
}

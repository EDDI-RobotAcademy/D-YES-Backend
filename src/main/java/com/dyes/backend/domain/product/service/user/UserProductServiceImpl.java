package com.dyes.backend.domain.product.service.user;

import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.FarmCustomerServiceInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmIntroductionInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmRepresentativeInfoRepository;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForUser;
import com.dyes.backend.domain.farmproducePriceForecast.service.request.FarmProducePriceForecastData;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.user.response.*;
import com.dyes.backend.domain.product.service.user.response.form.ProductListResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReadResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReviewResponseForUser;
import com.dyes.backend.domain.product.service.user.response.form.RandomProductListResponseFormForUser;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewRating;
import com.dyes.backend.domain.review.repository.ReviewRatingRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.utility.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.dyes.backend.domain.farm.entity.ProduceType.*;
import static com.dyes.backend.domain.product.entity.MaybeEventProduct.NO;
import static com.dyes.backend.utility.number.NumberUtils.findMinValue;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {
    final private ProductRepository productRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private ProductDetailImagesRepository productDetailImagesRepository;
    final private ProductManagementRepository productManagementRepository;
    final private FarmCustomerServiceInfoRepository farmCustomerServiceInfoRepository;
    final private FarmIntroductionInfoRepository farmIntroductionInfoRepository;
    final private FarmRepresentativeInfoRepository farmRepresentativeInfoRepository;
    final private ReviewRepository reviewRepository;
    final private ReviewRatingRepository reviewRatingRepository;
    final private RedisService redisService;

    // 상품 읽기
    @Override
    public ProductReadResponseFormForUser readProductForUser(Long productId) {
        log.info("Reading product with ID: {}", productId);

        // 상품 존재 여부 확인
        Optional<Product> maybeProduct = productRepository.findByIdWithFarm(productId);
        if (maybeProduct.isEmpty()) {
            log.info("Product with ID '{}' not found", productId);
            return null;
        }

        // 상품 읽기 진행
        try {
            Product product = maybeProduct.get();
            List<ProductOption> productOption = productOptionRepository.findByProduct(product);
            ProductMainImage productMainImage = productMainImageRepository.findByProduct(product).get();
            List<ProductDetailImages> productDetailImages = productDetailImagesRepository.findByProduct(product);

            Farm farm = product.getFarm();
            FarmCustomerServiceInfo farmCustomerServiceInfo = farmCustomerServiceInfoRepository.findByFarm(farm);
            FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(farm);

            List<Review> reviewList = reviewRepository.findAllByProduct(product);
            int totalReviewCount = 0;
            double averageRating;
            int totalRating = 0;
            for (Review review : reviewList) {
                totalReviewCount = totalReviewCount + 1;
                Optional<ReviewRating> maybeReviewRating = reviewRatingRepository.findByReview(review);
                if (maybeReviewRating.isPresent()) {
                    ReviewRating reviewRating = maybeReviewRating.get();
                    totalRating = totalRating + reviewRating.getRating();
                }
            }
            if (totalReviewCount == 0) {
                averageRating = totalRating;
            } else {
                averageRating = totalRating / totalReviewCount;
            }

            ProductResponseForUser productResponseForUser
                    = new ProductResponseForUser().productResponse(product);

            List<ProductOptionResponseForUser> productOptionResponseForUser
                    = new ProductOptionResponseForUser().productOptionResponseList(productOption);

            ProductMainImageResponseForUser productMainImageResponseForUser
                    = new ProductMainImageResponseForUser().productMainImageResponse(productMainImage);

            List<ProductDetailImagesResponseForUser> productDetailImagesResponseForUsers
                    = new ProductDetailImagesResponseForUser().productDetailImagesResponseList(productDetailImages);

            FarmInfoResponseForUser farmInfoResponseForUser
                    = new FarmInfoResponseForUser().farmInfoResponse(farm, farmCustomerServiceInfo, farmIntroductionInfo);

            ProductReviewResponseForUser productReviewResponseForUser =
                    new ProductReviewResponseForUser(totalReviewCount, averageRating);

            ProductReadResponseFormForUser responseForm
                    = new ProductReadResponseFormForUser(
                    productResponseForUser,
                    productOptionResponseForUser,
                    productMainImageResponseForUser,
                    productDetailImagesResponseForUsers,
                    farmInfoResponseForUser,
                    productReviewResponseForUser);

            log.info("Product read successful for product with ID: {}", productId);
            return responseForm;

        } catch (Exception e) {
            log.error("Failed to read the product: {}", e.getMessage(), e);
            return null;
        }
    }

    // 일반 사용자용 상품 목록 조회
    @Override
    public List<ProductListResponseFormForUser> getProductListForUser() {
        log.info("Reading product list");

        List<ProductListResponseFormForUser> productListResponseFormListForUser = new ArrayList<>();

        // 상품 목록 조회 진행
        try {
            List<Product> productList = productRepository.findAllWithFarm();
            for (Product product : productList) {
                if (product.getMaybeEventProduct().equals(NO)
                        && product.getProductSaleStatus().equals(SaleStatus.AVAILABLE)) {
                    ProductListResponseFormForUser productListResponseFormForUser = createUserProductListResponseForm(product);
                    productListResponseFormListForUser.add(productListResponseFormForUser);
                }
            }

            log.info("Product list read successful");
            return productListResponseFormListForUser;

        } catch (Exception e) {
            log.error("Failed to read the product list: {}", e.getMessage(), e);
            return null;
        }
    }

    // 일반 사용자용 랜덤 상품 목록 조회
    @Override
    public List<RandomProductListResponseFormForUser> getRandomProductListForUser() {
        log.info("Reading random product list");

        List<RandomProductListResponseFormForUser> randomProductListResponseFormListForUser = new ArrayList<>();

        // 랜덤 상품 목록 조회 진행
        try {
            List<Product> productList = productRepository.findByMaybeEventProduct(NO);
            Collections.shuffle(productList);
            for (Product product : productList) {

                List<Long> optionPriceList = new ArrayList<>();
                Long minOptionPrice = 0L;
                String productMainImage = null;
                boolean isSoldOut = false;

                // 상품 옵션 최저가 및 재고 조회
                List<ProductOption> productOptionList = productOptionRepository.findByProduct(product);
                for (ProductOption productOption : productOptionList) {
                    if (productOption.getStock() <= 0) {
                        isSoldOut = true;
                        break;
                    }
                }

                if (!isSoldOut) {
                    for (ProductOption productOption : productOptionList) {
                        optionPriceList.add(productOption.getOptionPrice());
                        minOptionPrice = findMinValue(optionPriceList);
                    }

                    Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProduct(product);
                    if (maybeProductMainImage.isPresent()) {
                        productMainImage = maybeProductMainImage.get().getMainImg();
                    }

                    RandomProductListResponseFormForUser randomProductListResponseFormForUser
                            = new RandomProductListResponseFormForUser(
                            product.getId(),
                            product.getProductName(),
                            productMainImage,
                            minOptionPrice);
                    randomProductListResponseFormListForUser.add(randomProductListResponseFormForUser);
                }

                if (randomProductListResponseFormListForUser.size() == 4) {
                    return randomProductListResponseFormListForUser;
                }
            }

            log.info("Product random list read successful");
            return randomProductListResponseFormListForUser;

        } catch (Exception e) {
            log.error("Failed to read the random product list: {}", e.getMessage(), e);
            return null;
        }
    }

    // 일반 사용자용 카테고리별 상품 목록 조회
    @Override
    public List<ProductListResponseFormForUser> getProductListByCategoryForUser(String category) {
        log.info("Reading product list by category");

        List<ProductListResponseFormForUser> productListResponseFormListForUser = new ArrayList<>();
        CultivationMethod cultivationMethod = CultivationMethod.valueOf(category);

        // 카테고리별 상품 목록 조회 진행
        try {
            List<Product> productList = productRepository.findAllWithFarmByCategory(cultivationMethod);
            for (Product product : productList) {
                ProductListResponseFormForUser productListResponseFormForUser = createUserProductListResponseForm(product);
                productListResponseFormListForUser.add(productListResponseFormForUser);
            }

            log.info("Product list by category read successful");
            return productListResponseFormListForUser;

        } catch (Exception e) {
            log.error("Failed to read the random product list: {}", e.getMessage(), e);
            return null;
        }
    }

    // 농가 지역별 상품 목록 조회
    @Override
    public List<ProductListResponseFormForUser> getProductListByRegionForUser(String region) {
        log.info("Reading product list by region");

        List<ProductListResponseFormForUser> productListResponseFormListForUser = new ArrayList<>();
        List<FarmCustomerServiceInfo> farmListByLocation = farmCustomerServiceInfoRepository.findByFarmAddressAddressContaining(region);

        // 지역별 상품 목록 조회 진행
        try {
            for (FarmCustomerServiceInfo farmByRegion : farmListByLocation) {
                List<Product> productList = productRepository.findAllByFarmWithFarm(farmByRegion.getFarm());
                for (Product product : productList) {
                    ProductListResponseFormForUser productListResponseFormForUser = createUserProductListResponseForm(product);
                    productListResponseFormListForUser.add(productListResponseFormForUser);
                }
            }

            log.info("Product list by region read successful");
            return productListResponseFormListForUser;

        } catch (Exception e) {
            log.error("Failed to read the random product list: {}", e.getMessage(), e);
            return null;
        }
    }

    // 신규 상품 10개 목록 조회
    @Override
    public List<ProductListResponseFormForUser> getNewProductListForUser() {
        log.info("Reading new product list");

        List<ProductListResponseFormForUser> productListResponseFormListForUser = new ArrayList<>();

        // 상품 목록 조회 진행
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minus(7, ChronoUnit.DAYS);
            log.info("start date : " + startDate);
            log.info("end date : " + endDate);

            List<ProductManagement> productManagementList = productManagementRepository.findAllByCreatedDateBetween(startDate, endDate);
            log.info("size : " + productManagementList.size());
            for (ProductManagement productManagement : productManagementList) {
                Product product = productManagement.getProduct();
                log.info("Is it Event Product? : {}", product.getMaybeEventProduct());
                log.info("Product Id is : {}", product.getId());
                if (product.getMaybeEventProduct().equals(NO)) {
                    log.info("This is not Event Product");
                    ProductListResponseFormForUser productListResponseFormForUser = createUserProductListResponseForm(product);
                    productListResponseFormListForUser.add(productListResponseFormForUser);
                }
            }

            log.info("New Product list read successful");
            return productListResponseFormListForUser;

        } catch (Exception e) {
            log.error("Failed to read the new product list: {}", e.getMessage(), e);
            return null;
        }
    }

    // 상품 목록 조회에 필요한 상품 정보 추출
    public ProductListResponseFormForUser createUserProductListResponseForm(Product product) {
        List<Long> optionPriceList = new ArrayList<>();
        Long minOptionPrice = 0L;
        int totalOptionStock = 0;
        boolean isSoldOut = false;
        String productMainImage = null;

        // 상품 옵션 최저가 및 재고 조회
        List<ProductOption> productOptionList = productOptionRepository.findByProduct(product);
        for (ProductOption productOption : productOptionList) {
            optionPriceList.add(productOption.getOptionPrice());
            minOptionPrice = findMinValue(optionPriceList);
            totalOptionStock = totalOptionStock + productOption.getStock();
        }
        if (totalOptionStock == 0) {
            isSoldOut = true;
        }

        Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProduct(product);
        if (maybeProductMainImage.isPresent()) {
            productMainImage = maybeProductMainImage.get().getMainImg();
        }

        LocalDate currentDate = LocalDate.now();
        ProduceType produceType = product.getProduceType();
        int roundedPriceChangePercentage = switch (produceType) {
            case CABBAGE, CARROT, CUCUMBER, KIMCHI_CABBAGE, ONION, POTATO, WELSH_ONION, YOUNG_PUMPKIN ->
                    calculatePriceChangePercentage(currentDate, produceType);
            default -> -999;
        };

        Farm farm = product.getFarm();
        FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(farm);
        FarmRepresentativeInfo farmRepresentativeInfo = farmRepresentativeInfoRepository.findByFarm(farm);

        List<Review> reviewList = reviewRepository.findAllByProduct(product);
        int totalReviewCount = 0;
        double averageRating;
        int totalRating = 0;
        for (Review review : reviewList) {
            totalReviewCount = totalReviewCount + 1;
            Optional<ReviewRating> maybeReviewRating = reviewRatingRepository.findByReview(review);
            if (maybeReviewRating.isPresent()) {
                ReviewRating reviewRating = maybeReviewRating.get();
                totalRating = totalRating + reviewRating.getRating();
            }
        }
        if (totalReviewCount == 0) {
            averageRating = totalRating;
        } else {
            averageRating = totalRating / totalReviewCount;
        }

        ProductResponseForListForUser productResponseForListForUser
                = new ProductResponseForListForUser(product.getId(), product.getProductName(), product.getCultivationMethod());

        ProductMainImageResponseForListForUser productMainImageResponseForListForUser
                = new ProductMainImageResponseForListForUser(productMainImage);

        ProductOptionResponseForListForUser productOptionResponseForListForUser
                = new ProductOptionResponseForListForUser(minOptionPrice, isSoldOut);

        FarmInfoResponseForListForUser farmInfoResponseForListForUser
                = new FarmInfoResponseForListForUser(farm.getFarmName(), farmIntroductionInfo.getMainImage(), farmRepresentativeInfo.getRepresentativeName());

        ProductReviewResponseForUser productReviewResponseForUser
                = new ProductReviewResponseForUser(totalReviewCount, averageRating);

        FarmProducePriceChangeInfoForListForUser farmProducePriceChangeInfoForListForUser
                = new FarmProducePriceChangeInfoForListForUser(roundedPriceChangePercentage);

        return new ProductListResponseFormForUser(
                productResponseForListForUser,
                productMainImageResponseForListForUser,
                productOptionResponseForListForUser,
                farmInfoResponseForListForUser,
                productReviewResponseForUser,
                farmProducePriceChangeInfoForListForUser);
    }

    private int calculatePriceChangePercentage(LocalDate currentDate, ProduceType produceType) {
        List<Product> productList = productRepository.findAllByProduceType(produceType);
        if (productList.size() == 0) {
            return -999;
        }

        double currentPriceValue = 0;
        double twoWeeksLaterPriceValue = 0;
        String produceTypeStr = produceType.toString().toLowerCase();

        if(produceType.equals(YOUNG_PUMPKIN)) {
            produceTypeStr = "youngPumpkin";
        } else if(produceType.equals(WELSH_ONION)) {
            produceTypeStr = "welshOnion";
        } else if(produceType.equals(KIMCHI_CABBAGE)) {
            produceTypeStr = "kimchiCabbage";
        }

        try {
            FarmProducePriceForecastData farmProducePriceForecastData =
                    redisService.getFarmProducePriceForecastData(produceTypeStr);
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            LocalDate after13daysDate = currentDate.plusDays(13);
            LocalDate tomorrowDate = currentDate.plusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedTomorrowDate = tomorrowDate.format(formatter);
            log.info("내일: " + formattedTomorrowDate);

            String formattedAfter13daysDate = after13daysDate.format(formatter);
            log.info("13일 후: " + formattedAfter13daysDate);

            Integer priceAfter13Days = priceListByDay.get(formattedAfter13daysDate);
            if (priceAfter13Days != null) {
                currentPriceValue = priceListByDay.get(formattedTomorrowDate);
                twoWeeksLaterPriceValue = priceAfter13Days;
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (currentPriceValue != 0 && twoWeeksLaterPriceValue != 0) {
            double priceChangePercentage = ((twoWeeksLaterPriceValue - currentPriceValue) / currentPriceValue) * 100;
            int roundedPriceChangePercentage = (int) Math.floor(priceChangePercentage);
            if (roundedPriceChangePercentage > 30) {
                roundedPriceChangePercentage = 30;
            } else if (roundedPriceChangePercentage < -30) {
                roundedPriceChangePercentage = -30;
            }

            log.info("가격 변동률: " + roundedPriceChangePercentage + "%");

            return roundedPriceChangePercentage;
        } else {
            return 0;
        }
    }
}

package com.dyes.backend.domain.product.service.user;

import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.FarmCustomerServiceInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmIntroductionInfoRepository;
import com.dyes.backend.domain.farm.repository.FarmRepresentativeInfoRepository;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForUser;
import com.dyes.backend.domain.farmproducePriceForecast.entity.*;
import com.dyes.backend.domain.farmproducePriceForecast.repository.*;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.user.response.ProductDetailImagesResponseForUser;
import com.dyes.backend.domain.product.service.user.response.ProductMainImageResponseForUser;
import com.dyes.backend.domain.product.service.user.response.ProductOptionResponseForUser;
import com.dyes.backend.domain.product.service.user.response.ProductResponseForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductListResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReadResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.RandomProductListResponseFormForUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    final private CabbagePriceRepository cabbagePriceRepository;
    final private CarrotPriceRepository carrotPriceRepository;
    final private CucumberPriceRepository cucumberPriceRepository;
    final private KimchiCabbagePriceRepository kimchiCabbagePriceRepository;
    final private OnionPriceRepository onionPriceRepository;
    final private PotatoPriceRepository potatoPriceRepository;
    final private WelshOnionPriceRepository welshOnionPriceRepository;
    final private YoungPumpkinPriceRepository youngPumpkinPriceRepository;

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

            ProductResponseForUser productResponseForUser
                    = new ProductResponseForUser().productResponse(product);

            List<ProductOptionResponseForUser> productOptionResponseForUser
                    = new ProductOptionResponseForUser().productOptionResponseList(productOption);

            ProductMainImageResponseForUser productMainImageResponseForUser
                    = new ProductMainImageResponseForUser().productMainImageResponse(productMainImage);

            List<ProductDetailImagesResponseForUser> productDetailImagesResponsForUsers
                    = new ProductDetailImagesResponseForUser().productDetailImagesResponseList(productDetailImages);

            FarmInfoResponseForUser farmInfoResponseForUser
                    = new FarmInfoResponseForUser().farmInfoResponse(farm, farmCustomerServiceInfo, farmIntroductionInfo);

            ProductReadResponseFormForUser responseForm
                    = new ProductReadResponseFormForUser(
                    productResponseForUser,
                    productOptionResponseForUser,
                    productMainImageResponseForUser,
                    productDetailImagesResponsForUsers,
                    farmInfoResponseForUser);

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
                ProductListResponseFormForUser productListResponseFormForUser = createUserProductListResponseForm(product);
                productListResponseFormListForUser.add(productListResponseFormForUser);
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
            List<Product> productList = productRepository.findAll();
            Collections.shuffle(productList);
            for (Product product : productList) {

                List<Long> optionPriceList = new ArrayList<>();
                Long minOptionPrice = 0L;
                String productMainImage = null;
                Boolean isSoldOut = false;

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
    public List<ProductListResponseFormForUser> getNew10ProductListForUser() {
        log.info("Reading new product list");

        List<ProductListResponseFormForUser> productListResponseFormListForUser = new ArrayList<>();

        // 상품 목록 조회 진행
        try {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductManagement> productManagementList = productManagementRepository.findNew10ByCreatedDate(pageable);
            for (ProductManagement productManagement : productManagementList) {
                Product product = productManagement.getProduct();
                ProductListResponseFormForUser productListResponseFormForUser = createUserProductListResponseForm(product);
                productListResponseFormListForUser.add(productListResponseFormForUser);
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
        Boolean isSoldOut = false;
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
        int roundedPriceChangePercentage = 0;
        switch (produceType) {
            case CABBAGE:
            case CARROT:
            case CUCUMBER:
            case KIMCHI_CABBAGE:
            case ONION:
            case POTATO:
            case WELSH_ONION:
            case YOUNG_PUMPKIN:
                roundedPriceChangePercentage = calculatePriceChangePercentage(currentDate, produceType);
                break;
            default:
                roundedPriceChangePercentage = -999;
                break;
        }

        Farm farm = product.getFarm();
        FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(farm);
        FarmRepresentativeInfo farmRepresentativeInfo = farmRepresentativeInfoRepository.findByFarm(farm);

        return new ProductListResponseFormForUser(
                product.getId(),
                product.getProductName(),
                product.getCultivationMethod(),
                productMainImage,
                minOptionPrice,
                isSoldOut,
                farm.getFarmName(),
                farmIntroductionInfo.getMainImage(),
                farmRepresentativeInfo.getRepresentativeName(),
                roundedPriceChangePercentage);
    }

    private int calculatePriceChangePercentage(LocalDate currentDate, ProduceType produceType) {
        List<Product> productList = productRepository.findAllByProduceType(produceType);
        if (productList.size() > 0) {
            double currentPriceValue = 0;
            double twoWeeksLaterPriceValue = 0;

            switch (produceType) {
                case CABBAGE:
                    Optional<CabbagePrice> maybeCabbagePrice = cabbagePriceRepository.findByDate(currentDate.toString());
                    Optional<CabbagePrice> maybeCabbagePriceTwoWeeksLater = cabbagePriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybeCabbagePrice.isPresent() && maybeCabbagePriceTwoWeeksLater.isPresent()) {
                        CabbagePrice currentPrice = maybeCabbagePrice.get();
                        CabbagePrice twoWeeksLaterPrice = maybeCabbagePriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                case CARROT:
                    Optional<CarrotPrice> maybeCarrotPrice = carrotPriceRepository.findByDate(currentDate.toString());
                    Optional<CarrotPrice> maybeCarrotPriceTwoWeeksLater = carrotPriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybeCarrotPrice.isPresent() && maybeCarrotPriceTwoWeeksLater.isPresent()) {
                        CarrotPrice currentPrice = maybeCarrotPrice.get();
                        CarrotPrice twoWeeksLaterPrice = maybeCarrotPriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                case CUCUMBER:
                    Optional<CucumberPrice> maybeCucumberPrice = cucumberPriceRepository.findByDate(currentDate.toString());
                    Optional<CucumberPrice> maybeCucumberPriceTwoWeeksLater = cucumberPriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybeCucumberPrice.isPresent() && maybeCucumberPriceTwoWeeksLater.isPresent()) {
                        CucumberPrice currentPrice = maybeCucumberPrice.get();
                        CucumberPrice twoWeeksLaterPrice = maybeCucumberPriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                case KIMCHI_CABBAGE:
                    Optional<KimchiCabbagePrice> maybeKimchiCabbagePrice = kimchiCabbagePriceRepository.findByDate(currentDate.toString());
                    Optional<KimchiCabbagePrice> maybeKimchiCabbagePriceTwoWeeksLater = kimchiCabbagePriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybeKimchiCabbagePrice.isPresent() && maybeKimchiCabbagePriceTwoWeeksLater.isPresent()) {
                        KimchiCabbagePrice currentPrice = maybeKimchiCabbagePrice.get();
                        KimchiCabbagePrice twoWeeksLaterPrice = maybeKimchiCabbagePriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                case ONION:
                    Optional<OnionPrice> maybeOnionPrice = onionPriceRepository.findByDate(currentDate.toString());
                    Optional<OnionPrice> maybeOnionPriceTwoWeeksLater = onionPriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybeOnionPrice.isPresent() && maybeOnionPriceTwoWeeksLater.isPresent()) {
                        OnionPrice currentPrice = maybeOnionPrice.get();
                        OnionPrice twoWeeksLaterPrice = maybeOnionPriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                case POTATO:
                    Optional<PotatoPrice> maybePotatoPrice = potatoPriceRepository.findByDate(currentDate.toString());
                    Optional<PotatoPrice> maybePotatoPriceTwoWeeksLater = potatoPriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybePotatoPrice.isPresent() && maybePotatoPriceTwoWeeksLater.isPresent()) {
                        PotatoPrice currentPrice = maybePotatoPrice.get();
                        PotatoPrice twoWeeksLaterPrice = maybePotatoPriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                case WELSH_ONION:
                    Optional<WelshOnionPrice> maybeWelshOnionPrice = welshOnionPriceRepository.findByDate(currentDate.toString());
                    Optional<WelshOnionPrice> maybeWelshOnionPriceTwoWeeksLater = welshOnionPriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybeWelshOnionPrice.isPresent() && maybeWelshOnionPriceTwoWeeksLater.isPresent()) {
                        WelshOnionPrice currentPrice = maybeWelshOnionPrice.get();
                        WelshOnionPrice twoWeeksLaterPrice = maybeWelshOnionPriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                case YOUNG_PUMPKIN:
                    Optional<YoungPumpkinPrice> maybeYoungPumpkinPrice = youngPumpkinPriceRepository.findByDate(currentDate.toString());
                    Optional<YoungPumpkinPrice> maybeYoungPumpkinPriceTwoWeeksLater = youngPumpkinPriceRepository.findByDate((currentDate.plusDays(13)).toString());
                    if (maybeYoungPumpkinPrice.isPresent() && maybeYoungPumpkinPriceTwoWeeksLater.isPresent()) {
                        YoungPumpkinPrice currentPrice = maybeYoungPumpkinPrice.get();
                        YoungPumpkinPrice twoWeeksLaterPrice = maybeYoungPumpkinPriceTwoWeeksLater.get();

                        currentPriceValue = currentPrice.getPrice();
                        twoWeeksLaterPriceValue = twoWeeksLaterPrice.getPrice();
                    }
                    break;
                default:
                    break;
            }
            double priceChangePercentage = ((twoWeeksLaterPriceValue - currentPriceValue) / currentPriceValue) * 100;
            int roundedPriceChangePercentage = (int) Math.floor(priceChangePercentage);
            if (roundedPriceChangePercentage > 30) {
                roundedPriceChangePercentage = 30;
            } else if (roundedPriceChangePercentage < -30) {
                roundedPriceChangePercentage = -30;
            }

            log.info("The percentage change in prices: " + roundedPriceChangePercentage + "%");

            return roundedPriceChangePercentage;
        }

        return -999;
    }
}

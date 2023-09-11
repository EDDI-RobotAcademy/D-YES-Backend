package com.dyes.backend.domain.product.service.user;

import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.*;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForUser;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.user.response.*;
import com.dyes.backend.domain.product.service.user.response.form.ProductListResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.ProductReadResponseFormForUser;
import com.dyes.backend.domain.product.service.user.response.form.RandomProductListResponseFormForUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.dyes.backend.utility.number.NumberUtils.findMinValue;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {
    final private ProductRepository productRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private ProductDetailImagesRepository productDetailImagesRepository;
    final private FarmCustomerServiceInfoRepository farmCustomerServiceInfoRepository;
    final private FarmIntroductionInfoRepository farmIntroductionInfoRepository;
    final private FarmRepresentativeInfoRepository farmRepresentativeInfoRepository;

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
                farmRepresentativeInfo.getRepresentativeName()
        );
    }
}

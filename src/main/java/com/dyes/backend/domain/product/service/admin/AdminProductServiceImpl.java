package com.dyes.backend.domain.product.service.admin;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.*;
import com.dyes.backend.domain.farm.service.request.FarmAuthenticationRequest;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForAdmin;
import com.dyes.backend.domain.farm.service.response.FarmInfoSummaryResponseForAdmin;
import com.dyes.backend.domain.product.controller.admin.form.ProductDeleteRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductListDeleteRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductModifyRequestForm;
import com.dyes.backend.domain.product.controller.admin.form.ProductRegisterRequestForm;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.admin.request.delete.ProductListDeleteRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductOptionRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductRegisterRequest;
import com.dyes.backend.domain.product.service.admin.response.*;
import com.dyes.backend.domain.product.service.admin.response.form.ProductInfoResponseFormForDashBoardForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductListResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductReadResponseFormForAdmin;
import com.dyes.backend.domain.product.service.admin.response.form.ProductSummaryReadResponseFormForAdmin;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.dyes.backend.domain.product.entity.SaleStatus.AVAILABLE;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
    final private ProductRepository productRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private ProductDetailImagesRepository productDetailImagesRepository;
    final private ProductManagementRepository productManagementRepository;
    final private FarmRepository farmRepository;
    final private FarmCustomerServiceInfoRepository farmCustomerServiceInfoRepository;
    final private FarmIntroductionInfoRepository farmIntroductionInfoRepository;
    final private ContainProductOptionRepository containProductOptionRepository;
    final private ReviewRepository reviewRepository;
    final private AdminService adminService;

    // 상품 등록
    @Override
    public boolean registerProduct(ProductRegisterRequestForm registerForm) {
        log.info("Registering a new product");

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = registerForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }

        // 등록된 농가 존재 여부 확인
        FarmAuthenticationRequest farmAuthenticationRequest = registerForm.toFarmAuthenticationRequest();

        final String farmName = farmAuthenticationRequest.getFarmName();

        Optional<Farm> maybeFarm = farmRepository.findByFarmName(farmName);
        if (maybeFarm.isEmpty()) {
            log.info("Farm with name '{}' not found", farmName);
            return false;
        }

        Farm farm = maybeFarm.get();

        // 상품 등록 진행
        ProductRegisterRequest productRequest = registerForm.getProductRegisterRequest();
        ProductMainImageRegisterRequest productMainImageRegisterRequest = registerForm.getProductMainImageRegisterRequest();
        List<ProductDetailImagesRegisterRequest> productDetailImagesRegisterRequests = registerForm.getProductDetailImagesRegisterRequests();
        List<ProductOptionRegisterRequest> productOptionRegisterRequests = registerForm.getProductOptionRegisterRequest();

        // 등록하려고 하는 옵션명이 동일한지 확인
        Set<String> optionNames = new HashSet<>();
        for (ProductOptionRegisterRequest request : productOptionRegisterRequests) {
            if (optionNames.contains(request.getOptionName())) {
                log.info("Duplicate option name found: {}", request.getOptionName());
                return false;
            }
            optionNames.add(request.getOptionName());
        }

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

            for (int i = 0; i < productOptionRegisterRequests.size(); i++) {
                ProductOption productOption = ProductOption.builder()
                        .optionPrice(productOptionRegisterRequests.get(i).getOptionPrice())
                        .stock(productOptionRegisterRequests.get(i).getStock())
                        .optionName(productOptionRegisterRequests.get(i).getOptionName())
                        .amount(Amount.builder()
                                .value(productOptionRegisterRequests.get(i).getValue())
                                .unit(productOptionRegisterRequests.get(i).getUnit())
                                .build())
                        .product(product)
                        .optionSaleStatus(AVAILABLE)
                        .build();

                productOptionRepository.save(productOption);
            }

            ProductMainImage mainImage = ProductMainImage.builder()
                    .id(product.getId())
                    .mainImg(productMainImageRegisterRequest.getMainImg())
                    .product(product)
                    .build();

            productMainImageRepository.save(mainImage);

            for (ProductDetailImagesRegisterRequest detailImagesInRegisterForm : productDetailImagesRegisterRequests) {
                ProductDetailImages detailImages = ProductDetailImages.builder()
                        .detailImgs(detailImagesInRegisterForm.getDetailImgs())
                        .product(product)
                        .build();

                productDetailImagesRepository.save(detailImages);
            }

            log.info("Product registration successful");
            return true;

        } catch (Exception e) {
            log.error("Failed to register the product: {}", e.getMessage(), e);
            return false;
        }
    }

    // 상품 수정
    @Override
    public boolean modifyProduct(Long productId, ProductModifyRequestForm modifyForm) {
        log.info("Modifying product with ID: {}", productId);

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = modifyForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }

        // 상품 존재 여부 확인
        final Long modifyProductId = productId;

        Optional<Product> maybeProduct = productRepository.findById(modifyProductId);
        if (maybeProduct.isEmpty()) {
            log.info("Product with ID '{}' not found", productId);
            return false;
        }

        // 상품 수정 진행
        ProductModifyRequest productModifyRequest = modifyForm.getProductModifyRequest();
        ProductMainImageModifyRequest productMainImageModifyRequest = modifyForm.getProductMainImageModifyRequest();
        List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequestList = modifyForm.getProductDetailImagesModifyRequest();
        List<ProductOptionModifyRequest> productOptionModifyRequestList = modifyForm.getProductOptionModifyRequest();

        try {
            Product modifyProduct = maybeProduct.get();
            modifyProduct.setProductName(productModifyRequest.getProductName());
            modifyProduct.setProductDescription(productModifyRequest.getProductDescription());
            modifyProduct.setCultivationMethod(productModifyRequest.getCultivationMethod());
            modifyProduct.setProductSaleStatus(productModifyRequest.getProductSaleStatus());
            productRepository.save(modifyProduct);

            final Long modifyProductMainImageId = productMainImageModifyRequest.getMainImageId();
            Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findById(modifyProductMainImageId);
            if (maybeProductMainImage.isEmpty()) {
                log.info("ProductMainImage for product with ID '{}' is empty", modifyProductMainImageId);
                return false;
            }

            ProductMainImage modifyProductMainImage = maybeProductMainImage.get();
            modifyProductMainImage.setMainImg(productMainImageModifyRequest.getMainImg());
            modifyProductMainImage.setProduct(modifyProduct);
            productMainImageRepository.save(modifyProductMainImage);

            // DB에 저장된 상품 상세 이미지 가져오기
            List<ProductDetailImages> productDetailImagesList = productDetailImagesRepository.findByProductWithProduct(modifyProduct);
            List<Long> savedProductDetailImagesList = new ArrayList<>();
            for (ProductDetailImages savedProductDetailImages : productDetailImagesList) {
                savedProductDetailImagesList.add(savedProductDetailImages.getId());
            }

            // 수정 요청된 상품 상세 이미지 가져오기
            List<Long> modifyProductDetailImagesList = new ArrayList<>();
            for (ProductDetailImagesModifyRequest modifyProductDetailImages : productDetailImagesModifyRequestList) {
                modifyProductDetailImagesList.add(modifyProductDetailImages.getDetailImageId());
            }

            // 삭제 요청된 이미지 삭제
            List<Long> needRemoveProductDetailImagesList = new ArrayList<>(savedProductDetailImagesList);
            if (savedProductDetailImagesList.size() > modifyProductDetailImagesList.size()) {
                needRemoveProductDetailImagesList.removeAll(modifyProductDetailImagesList);
                for (Long removeProductDetailImages : needRemoveProductDetailImagesList) {
                    productDetailImagesRepository.deleteById(removeProductDetailImages);
                }
            }

            // 삭제 후 나머지 이미지는 수정 혹은 생성 진행
            for (ProductDetailImagesModifyRequest productDetailImage : productDetailImagesModifyRequestList) {
                final Long productDetailImageId = productDetailImage.getDetailImageId();
                Optional<ProductDetailImages> maybeProductDetailImages = productDetailImagesRepository.findById(productDetailImageId);
                if (maybeProductDetailImages.isEmpty()) {
                    log.info("ProductDetailImages is empty, Register new ProductDetailImages");
                    ProductDetailImages newProductDetailImages = ProductDetailImages.builder()
                            .detailImgs(productDetailImage.getDetailImgs())
                            .product(modifyProduct)
                            .build();

                    productDetailImagesRepository.save(newProductDetailImages);
                } else if (maybeProductDetailImages.isPresent()) {
                    ProductDetailImages modifyProductDetailImages = maybeProductDetailImages.get();
                    modifyProductDetailImages.setDetailImgs(productDetailImage.getDetailImgs());
                    modifyProductDetailImages.setProduct(modifyProduct);
                    productDetailImagesRepository.save(modifyProductDetailImages);
                }
            }

            // DB에 저장된 상품 옵션 리스트 가져오기
            List<ProductOption> productOptionList = productOptionRepository.findByProductWithProduct(modifyProduct);
            List<Long> savedOptionList = new ArrayList<>();
            for (ProductOption savedProductOption : productOptionList) {
                savedOptionList.add(savedProductOption.getId());
            }

            // 수정 요청 온 상품 옵션 리스트 가져오기
            List<Long> modifyOptionList = new ArrayList<>();
            for (ProductOptionModifyRequest modifyProductOption : productOptionModifyRequestList) {
                modifyOptionList.add(modifyProductOption.getOptionId());
            }

            // 옵션 개수가 줄어서 삭제가 필요할 때
            List<Long> needRemoveOptionList = new ArrayList<>(savedOptionList);

            if (savedOptionList.size() > modifyOptionList.size()) {
                needRemoveOptionList.removeAll(modifyOptionList);
                for (Long removeOption : needRemoveOptionList) {
                    productOptionRepository.deleteById(removeOption);
                }
            }

            // 삭제 후 나머지 요청 옵션은 수정 혹은 생성 진행
            for (ProductOptionModifyRequest productOption : productOptionModifyRequestList) {
                final Long productOptionId = productOption.getOptionId();
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    log.info("ProductOption with ID '{}' not found", productOptionId);

                    ProductOption newProductOption = ProductOption.builder()
                            .optionPrice(productOption.getOptionPrice())
                            .stock(productOption.getStock())
                            .optionName(productOption.getOptionName())
                            .amount(Amount.builder()
                                    .value(productOption.getValue())
                                    .unit(productOption.getUnit())
                                    .build())
                            .product(modifyProduct)
                            .optionSaleStatus(AVAILABLE)
                            .build();

                    productOptionRepository.save(newProductOption);
                } else if (maybeProductOption.isPresent()) {
                    ProductOption modifyProductOption = maybeProductOption.get();
                    modifyProductOption.setOptionName(productOption.getOptionName());
                    modifyProductOption.setOptionPrice(productOption.getOptionPrice());
                    modifyProductOption.setStock(productOption.getStock());
                    modifyProductOption.setAmount(new Amount(productOption.getValue(), productOption.getUnit()));
                    modifyProductOption.setOptionSaleStatus(productOption.getOptionSaleStatus());
                    modifyProductOption.setProduct(modifyProduct);
                    productOptionRepository.save(modifyProductOption);
                }
            }
            log.info("Product modification successful for product with ID: {}", productId);
            return true;

        } catch (Exception e) {
            log.error("Failed to modify the product: {}", e.getMessage(), e);
            return false;
        }
    }

    // 상품 삭제
    @Override
    public boolean deleteProduct(Long productId, ProductDeleteRequestForm deleteForm) {
        log.info("Deleting product with ID: {}", productId);

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = deleteForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }

        // 상품 존재 여부 확인
        Optional<Product> maybeProduct = productRepository.findByIdWithFarm(productId);
        if (maybeProduct.isEmpty()) {
            log.info("Product with ID '{}' not found", productId);
            return false;
        }
        Product deleteProduct = maybeProduct.get();

        // 상품 삭제 진행
        try {
            List<Review> reviewList = reviewRepository.findAllByProduct(deleteProduct);
            if(reviewList.size() > 0) {
                log.info("Unable to delete the product: Reviews are registered");
                return false;
            }
            List<ProductOption> deleteProductOptionList = productOptionRepository.findByProductWithProduct(deleteProduct);
            for (ProductOption productOption : deleteProductOptionList) {
                List<ContainProductOption> containProductOptionList = containProductOptionRepository.findAllByOptionId(productOption.getId());
                if (containProductOptionList.size() > 0) {
                    for (ContainProductOption containProductOption : containProductOptionList) {
                        containProductOption.setOptionId(0L);
                        containProductOptionRepository.save(containProductOption);
                        log.info("Option with ID '{}' is included in the cart", productOption.getId());
                    }
                }
                productOptionRepository.delete(productOption);
                log.info("Option with ID '{}' has been deleted.", productOption.getId());
            }

            Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProductWithProduct(deleteProduct);
            if (maybeProductMainImage.isEmpty()) {
                log.info("ProductMainImage for product with ID '{}' is empty", deleteProduct.getId());
            } else if(maybeProductMainImage.isPresent()){
                ProductMainImage deleteProductMainImage = maybeProductMainImage.get();
                productMainImageRepository.delete(deleteProductMainImage);
            }

            List<ProductDetailImages> deleteProductDetailImagesList = productDetailImagesRepository.findByProductWithProduct(deleteProduct);
            for (ProductDetailImages productDetailImages : deleteProductDetailImagesList) {
                productDetailImagesRepository.delete(productDetailImages);
            }

            Optional<ProductManagement> maybeProductManagement = productManagementRepository.findByProduct(deleteProduct);
            if(maybeProductManagement.isEmpty()) {
                log.info("ProductManagement for product with ID '{}' is empty", deleteProduct.getId());
            } else if(maybeProductMainImage.isPresent()){
                ProductMainImage deleteProductMainImage = maybeProductMainImage.get();
                productMainImageRepository.delete(deleteProductMainImage);
            }

            productRepository.delete(deleteProduct);
            log.info("Product deletion successful for product with ID: {}", productId);
            return true;

        } catch (Exception e) {
            log.error("Failed to delete the product: {}", e.getMessage(), e);
            return false;
        }
    }

    // 상품 여러 개 삭제
    @Override
    public boolean deleteProductList(ProductListDeleteRequestForm listDeleteForm) {
        log.info("Deleting product with ID: {}", listDeleteForm.getProductIdList());

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = listDeleteForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }

        ProductListDeleteRequest productListDeleteRequest = listDeleteForm.toProductListDeleteRequest();

        // 상품 삭제 진행
        try {
            List<Product> productList = productRepository.findAllByIdWithFarm(productListDeleteRequest.getProductIdList());
            for (Product deleteProduct : productList) {
                List<Review> reviewList = reviewRepository.findAllByProduct(deleteProduct);
                if(reviewList.size() > 0) {
                    log.info("Unable to delete the product: Reviews are registered");
                    return false;
                }
                List<ProductOption> deleteProductOptionList = productOptionRepository.findByProductWithProduct(deleteProduct);
                for (ProductOption productOption : deleteProductOptionList) {
                    List<ContainProductOption> containProductOptionList = containProductOptionRepository.findAllByOptionId(productOption.getId());
                    if (containProductOptionList.size() > 0) {
                        for (ContainProductOption containProductOption : containProductOptionList) {
                            containProductOption.setOptionId(0L);
                            containProductOptionRepository.save(containProductOption);
                            log.info("Option with ID '{}' is included in the cart", productOption.getId());
                        }
                    }
                    productOptionRepository.delete(productOption);
                    log.info("Option with ID '{}' has been deleted.", productOption.getId());
                }

                Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProductWithProduct(deleteProduct);
                if (maybeProductMainImage.isEmpty()) {
                    log.info("ProductMainImage for product with ID '{}' is empty", deleteProduct.getId());

                } else if(maybeProductMainImage.isPresent()) {
                    ProductMainImage deleteProductMainImage = maybeProductMainImage.get();
                    productMainImageRepository.delete(deleteProductMainImage);
                }

                List<ProductDetailImages> deleteProductDetailImagesList = productDetailImagesRepository.findByProductWithProduct(deleteProduct);
                for (ProductDetailImages productDetailImages : deleteProductDetailImagesList) {
                    productDetailImagesRepository.delete(productDetailImages);
                }

                Optional<ProductManagement> maybeProductManagement = productManagementRepository.findByProduct(deleteProduct);
                if(maybeProductManagement.isEmpty()) {
                    log.info("ProductManagement for product with ID '{}' is empty", deleteProduct.getId());
                } else if(maybeProductManagement.isPresent()) {
                    ProductManagement productManagement = maybeProductManagement.get();
                    productManagementRepository.delete(productManagement);
                }

                log.info("Product deletion successful for product with ID: {}", deleteProduct.getId());
                productRepository.delete(deleteProduct);
            }
            return true;

        } catch (Exception e) {
            log.error("Failed to delete the product: {}", e.getMessage(), e);
            return false;
        }
    }

    // 관리자의 상품 읽기
    @Override
    public ProductReadResponseFormForAdmin readProductForAdmin(Long productId) {
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

            ProductResponseForAdmin productResponseForAdmin
                    = new ProductResponseForAdmin().productResponseForAdmin(product);

            List<ProductOptionResponseForAdmin> productOptionResponseForAdmin
                    = new ProductOptionResponseForAdmin().productOptionResponseForAdmin(productOption);

            ProductMainImageResponseForAdmin productMainImageResponseForAdmin
                    = new ProductMainImageResponseForAdmin().productMainImageResponseForAdmin(productMainImage);

            List<ProductDetailImagesResponseForAdmin> productDetailImagesResponsesForAdmin
                    = new ProductDetailImagesResponseForAdmin().productDetailImagesResponseForAdminList(productDetailImages);

            FarmInfoResponseForAdmin farmInfoResponseForAdmin
                    = new FarmInfoResponseForAdmin(
                    farm.getId(),
                    farm.getFarmName(),
                    farmCustomerServiceInfo.getCsContactNumber(),
                    farmCustomerServiceInfo.getFarmAddress(),
                    farmIntroductionInfo.getMainImage(),
                    farmIntroductionInfo.getIntroduction(),
                    farmIntroductionInfo.getProduceTypes());

            ProductReadResponseFormForAdmin responseForm
                    = new ProductReadResponseFormForAdmin(
                    productResponseForAdmin,
                    productOptionResponseForAdmin,
                    productMainImageResponseForAdmin,
                    productDetailImagesResponsesForAdmin,
                    farmInfoResponseForAdmin);

            log.info("Product read successful for product with ID: {}", productId);
            return responseForm;

        } catch (Exception e) {
            log.error("Failed to read the product: {}", e.getMessage(), e);
            return null;
        }
    }

    // 관리자용 상품 목록 조회
    @Override
    public List<ProductListResponseFormForAdmin> getProductListForAdmin() {
        log.info("Reading product list");

        List<ProductListResponseFormForAdmin> productListResponseFormListForAdmin = new ArrayList<>();

        // 상품 목록 조회 진행
        try {
            List<Product> productList = productRepository.findAllWithFarm();
            for (Product product : productList) {

                List<ProductOptionListResponseForAdmin> adminProductOptionListResponseList = new ArrayList<>();

                List<ProductOption> productOptionList = productOptionRepository.findByProduct(product);
                for (ProductOption productOption : productOptionList) {
                    ProductOptionListResponseForAdmin adminProductOptionListResponse
                            = new ProductOptionListResponseForAdmin().productOptionListResponseForAdmin(productOption);

                    adminProductOptionListResponseList.add(adminProductOptionListResponse);
                }

                Farm farm = product.getFarm();
                ProductListResponseFormForAdmin productListResponseFormForAdmin
                        = new ProductListResponseFormForAdmin(
                        product.getId(),
                        product.getProductName(),
                        product.getProductSaleStatus(),
                        adminProductOptionListResponseList,
                        farm.getFarmName());
                productListResponseFormListForAdmin.add(productListResponseFormForAdmin);
            }

            log.info("Product list read successful");
            return productListResponseFormListForAdmin;

        } catch (Exception e) {
            log.error("Failed to read the product list: {}", e.getMessage(), e);
            return null;
        }
    }

    // 상품 삭제 전 요약정보 확인
    @Override
    public ProductSummaryReadResponseFormForAdmin readProductSummaryForAdmin(Long productId) {
        log.info("Reading product summary with ID: {}", productId);

        // 상품 존재 여부 확인
        Optional<Product> maybeProduct = productRepository.findByIdWithFarm(productId);
        if (maybeProduct.isEmpty()) {
            log.info("Product with ID '{}' not found", productId);
        }

        // 상품 요약정보 읽기 진행
        try {
            Product product = maybeProduct.get();
            ProductSummaryResponseForAdmin productSummaryResponseForAdmin
                    = new ProductSummaryResponseForAdmin().productSummaryResponseForAdmin(product);

            List<ProductOption> productOptionList = productOptionRepository.findByProduct(product);
            List<ProductOptionSummaryResponseForAdmin> productOptionSummaryResponseForAdminList = new ArrayList<>();
            for (ProductOption productOption : productOptionList) {
                ProductOptionSummaryResponseForAdmin productOptionSummaryResponseForAdmin
                        = new ProductOptionSummaryResponseForAdmin().productOptionSummaryResponseForAdmin(productOption);
                productOptionSummaryResponseForAdminList.add(productOptionSummaryResponseForAdmin);
            }

            Farm farm = product.getFarm();
            FarmInfoSummaryResponseForAdmin farmInfoSummaryResponseForAdmin
                    = new FarmInfoSummaryResponseForAdmin().farmInfoSummaryResponseForAdmin(farm);

            ProductSummaryReadResponseFormForAdmin productSummaryReadResponseFormForAdmin
                    = new ProductSummaryReadResponseFormForAdmin(
                    productSummaryResponseForAdmin,
                    productOptionSummaryResponseForAdminList,
                    farmInfoSummaryResponseForAdmin);

            log.info("Product summary read successful");
            return productSummaryReadResponseFormForAdmin;

        } catch (Exception e) {
            log.error("Failed to read the product summary: {}", e.getMessage(), e);
            return null;
        }
    }

    // 신규 상품 목록 조회
    @Override
    public ProductInfoResponseFormForDashBoardForAdmin getNewProductListForAdmin() {
        log.info("Finding New registration Product start");

        // 최종적으로 반환할 ResponseForm에 들어갈 Response
        List<ProductManagementInfoResponseForAdmin> registeredProductCountList = new ArrayList<>();
        List<ProductInfoResponseForAdmin> productInfoResponseForAdminList = new ArrayList<>();

        // 이전 7일간의 내역을 조회
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        List<ProductManagement> productManagementList
                = productManagementRepository.findAllByCreatedDateAfterOrderByCreatedDateDesc(sevenDaysAgo);
        if(productManagementList.size() == 0) {
            log.info("No Products found.");
            return null;
        }
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> productCountList = new ArrayList<>();
        int registeredProductCountToday = 0;
        int registeredProductCount1DayAgo = 0;
        int registeredProductCount2DaysAgo = 0;
        int registeredProductCount3DaysAgo = 0;
        int registeredProductCount4DaysAgo = 0;
        int registeredProductCount5DaysAgo = 0;
        int registeredProductCount6DaysAgo = 0;

        // 상품 목록 조회 진행
        for (ProductManagement productManagement : productManagementList) {
            Product product = productManagement.getProduct();

            if (productManagement.getCreatedDate().equals(today)) {
                registeredProductCountToday = registeredProductCountToday + 1;
            } else if (productManagement.getCreatedDate().equals(today.minusDays(1))) {
                registeredProductCount1DayAgo = registeredProductCount1DayAgo + 1;
            } else if (productManagement.getCreatedDate().equals(today.minusDays(2))) {
                registeredProductCount2DaysAgo = registeredProductCount2DaysAgo + 1;
            } else if (productManagement.getCreatedDate().equals(today.minusDays(3))) {
                registeredProductCount3DaysAgo = registeredProductCount3DaysAgo + 1;
            } else if (productManagement.getCreatedDate().equals(today.minusDays(4))) {
                registeredProductCount4DaysAgo = registeredProductCount4DaysAgo + 1;
            } else if (productManagement.getCreatedDate().equals(today.minusDays(5))) {
                registeredProductCount5DaysAgo = registeredProductCount5DaysAgo + 1;
            } else if (productManagement.getCreatedDate().equals(today.minusDays(6))) {
                registeredProductCount6DaysAgo = registeredProductCount6DaysAgo + 1;
            }
            Farm farm = product.getFarm();
            ProductInfoResponseForAdmin productInfoResponseForAdmin
                    = new ProductInfoResponseForAdmin(product.getId(), product.getProductName(), product.getProductSaleStatus(), productManagement.getCreatedDate(), farm.getFarmName());
            productInfoResponseForAdminList.add(productInfoResponseForAdmin);

        }
        dateList.add(today);
        dateList.add(today.minusDays(1));
        dateList.add(today.minusDays(2));
        dateList.add(today.minusDays(3));
        dateList.add(today.minusDays(4));
        dateList.add(today.minusDays(5));
        dateList.add(today.minusDays(6));

        productCountList.add(registeredProductCountToday);
        productCountList.add(registeredProductCount1DayAgo);
        productCountList.add(registeredProductCount2DaysAgo);
        productCountList.add(registeredProductCount3DaysAgo);
        productCountList.add(registeredProductCount4DaysAgo);
        productCountList.add(registeredProductCount5DaysAgo);
        productCountList.add(registeredProductCount6DaysAgo);

        for (int i = 0; i < 7; i++) {
            ProductManagementInfoResponseForAdmin productManagementInfoResponseForAdmin
                    = new ProductManagementInfoResponseForAdmin(dateList.get(i), productCountList.get(i));
            registeredProductCountList.add(productManagementInfoResponseForAdmin);
        }

        ProductInfoResponseFormForDashBoardForAdmin productInfoResponseFormForDashBoardForAdmin
                = new ProductInfoResponseFormForDashBoardForAdmin(productInfoResponseForAdminList, registeredProductCountList);

        log.info("Finding New registration Products successful");
        return productInfoResponseFormForDashBoardForAdmin;
    }
}

package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmOperation;
import com.dyes.backend.domain.farm.repository.FarmOperationRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.product.controller.form.ProductDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductListDeleteForm;
import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.Response.*;
import com.dyes.backend.domain.product.service.request.*;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dyes.backend.domain.product.entity.SaleStatus.AVAILABLE;
import static com.dyes.backend.utility.number.NumberUtils.findMinValue;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    final private ProductRepository productRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private ProductDetailImagesRepository productDetailImagesRepository;
    final private FarmRepository farmRepository;
    final private FarmOperationRepository farmOperationRepository;
    final private AdminService adminService;

    // 상품 등록
    @Override
    public boolean productRegistration(ProductRegisterForm registerForm) {
        log.info("productRegistration start");

        final Admin admin = adminService.findAdminByUserToken(registerForm.getUserToken());

        if(admin == null) {
            log.info("Can not find Admin");
            return false;
        }

        final String farmName = registerForm.getFarmName();

        Optional<Farm> maybeFarm = farmRepository.findByFarmName(farmName);
        if(maybeFarm.isEmpty()) {
            log.info("Can not find Farm");
            return false;
        }
        Farm farm = maybeFarm.get();

        ProductRegisterRequest productRequest = registerForm.getProductRegisterRequest();
        List<ProductOptionRegisterRequest> productOptionRegisterRequests = registerForm.getProductOptionRegisterRequest();
        ProductMainImageRegisterRequest productMainImageRegisterRequest = registerForm.getProductMainImageRegisterRequest();
        List<ProductDetailImagesRegisterRequest> productDetailImagesRegisterRequests = registerForm.getProductDetailImagesRegisterRequests();

        try {
            Product product = Product.builder()
                    .productName(productRequest.getProductName())
                    .productDescription(productRequest.getProductDescription())
                    .cultivationMethod(cultivationMethodDecision(productRequest.getCultivationMethod()))
                    .productSaleStatus(AVAILABLE)
                    .farm(farm)
                    .build();
            log.info("product: " + product);
            productRepository.save(product);

            for (int i = 0; i < productOptionRegisterRequests.size(); i++) {
                ProductOption productOption = ProductOption.builder()
                        .optionPrice(productOptionRegisterRequests.get(i).getOptionPrice())
                        .stock(productOptionRegisterRequests.get(i).getStock())
                        .optionName(productOptionRegisterRequests.get(i).getOptionName())
                        .amount(Amount.builder()
                                .value(productOptionRegisterRequests.get(i).getValue())
                                .unit(unitDecision(productOptionRegisterRequests.get(i).getUnit()))
                                .build())
                        .product(product)
                        .optionSaleStatus(AVAILABLE)
                        .build();
                log.info("productOption: " + productOption);
                productOptionRepository.save(productOption);
            }

            ProductMainImage mainImage = ProductMainImage.builder()
                    .id(product.getId())
                    .mainImg(productMainImageRegisterRequest.getMainImg())
                    .product(product)
                    .build();
            log.info("mainImage: " + mainImage);
            productMainImageRepository.save(mainImage);

            for (ProductDetailImagesRegisterRequest detailImagesInRegisterForm : productDetailImagesRegisterRequests) {
                ProductDetailImages detailImages = ProductDetailImages.builder()
                        .detailImgs(detailImagesInRegisterForm.getDetailImgs())
                        .product(product)
                        .build();
                log.info("detailImages: " + detailImages);
                productDetailImagesRepository.save(detailImages);
            }
            log.info("productRegistration end");
            return true;
        } catch (Exception e) {
            log.error("Can't register this product: {}", e.getMessage(), e);
            log.info("productRegistration end");
            return false;
        }
    }

    // 상품 읽기
    @Override
    public UserProductResponseForm readProduct(Long productId) {
        log.info("readProduct start");
        try {
            Optional<Product> maybeProduct = productRepository.findByIdWithFarm(productId);
            if(maybeProduct.isEmpty()) {
                log.info("Can not find Product");
                return null;
            }
            Product product = maybeProduct.get();
            log.info("product: " + product);
            List<ProductOption> productOption = productOptionRepository.findByProduct(product);
            log.info("productOption: " + productOption);
            ProductMainImage productMainImage = productMainImageRepository.findByProduct(product).get();
            log.info("productMainImage: " + productMainImage);
            List<ProductDetailImages> productDetailImages = productDetailImagesRepository.findByProduct(product);
            log.info("productDetailImages: " + productDetailImages);
            Farm farm = product.getFarm();
            log.info("farm: " + farm);

            ProductResponse productResponse = new ProductResponse().productResponse(product);
            List<ProductOptionResponse> productOptionResponse = new ProductOptionResponse().productOptionResponseList(productOption);
            ProductMainImageResponse productMainImageResponse = new ProductMainImageResponse().productMainImageResponse(productMainImage);
            List<ProductDetailImagesResponse> productDetailImagesResponses = new ProductDetailImagesResponse().productDetailImagesResponseList(productDetailImages);
            FarmInfoResponse farmInfoResponse = new FarmInfoResponse().farmInfoResponse(farm);
            UserProductResponseForm responseForm = new UserProductResponseForm(productResponse, productOptionResponse, productMainImageResponse, productDetailImagesResponses, farmInfoResponse);
            log.info("responseForm: " + responseForm);

            log.info("readProduct end");
            return responseForm;
        } catch (Exception e) {
            log.error("Can't register this product: {}", e.getMessage(), e);
            log.info("readProduct end");
            return null;
        }
    }

    // 상품 수정
    @Override
    public boolean productModify(Long productId, ProductModifyForm modifyForm) {
        final Admin admin = adminService.findAdminByUserToken(modifyForm.getUserToken());

        if(admin == null) {
            log.info("Can not find Admin");
            return false;
        }

        ProductModifyRequest productModifyRequest = modifyForm.getProductModifyRequest();
        ProductMainImageModifyRequest productMainImageModifyRequest = modifyForm.getProductMainImageModifyRequest();
        List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequestList = modifyForm.getProductDetailImagesModifyRequest();
        List<ProductOptionModifyRequest> productOptionModifyRequestList = modifyForm.getProductOptionModifyRequest();

        // 상품 기본 정보 업데이트
        final Long modifyProductId = productId;
        Optional<Product> maybeProduct = productRepository.findById(modifyProductId);
        if(maybeProduct.isEmpty()) {
            log.info("Product is empty");
            return false;
        }

        Product modifyProduct = maybeProduct.get();
        modifyProduct.setProductName(productModifyRequest.getProductName());
        modifyProduct.setProductDescription(productModifyRequest.getProductDescription());
        modifyProduct.setCultivationMethod(productModifyRequest.getCultivationMethod());
        productRepository.save(modifyProduct);

        // 상품 메인 이미지 업데이트
        final Long modifyProductMainImageId = productMainImageModifyRequest.getProductMainImageId();
        Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findById(modifyProductMainImageId);
        if(maybeProductMainImage.isEmpty()) {
            log.info("ProductMainImage is empty");
            return false;
        }

        ProductMainImage modifyProductMainImage = maybeProductMainImage.get();
        modifyProductMainImage.setMainImg(productMainImageModifyRequest.getMainImg());
        modifyProductMainImage.setProduct(modifyProduct);
        productMainImageRepository.save(modifyProductMainImage);

        // 상품 상세 이미지 업데이트
        for(ProductDetailImagesModifyRequest productDetailImage: productDetailImagesModifyRequestList) {
            final Long productDetailImageId = productDetailImage.getProductDetailImageId();
            Optional<ProductDetailImages> maybeProductDetailImages = productDetailImagesRepository.findById(productDetailImageId);
            if(maybeProductDetailImages.isEmpty()) {
                log.info("ProductDetailImages is empty");
                return false;
            }
            ProductDetailImages modifyProductDetailImages = maybeProductDetailImages.get();
            modifyProductDetailImages.setDetailImgs(productDetailImage.getDetailImgs());
            modifyProductDetailImages.setProduct(modifyProduct);
            productDetailImagesRepository.save(modifyProductDetailImages);
        }

        // 상품 옵션 업데이트
        for(ProductOptionModifyRequest productOption: productOptionModifyRequestList) {
            final Long productOptionId = productOption.getOptionId();
            Optional<ProductOption> maybeProductOption = productOptionRepository.findById(productOptionId);
            if(maybeProductOption.isEmpty()) {
                log.info("ProductOption is empty");
                return false;
            }
            ProductOption modifyProductOption = maybeProductOption.get();
            modifyProductOption.setOptionName(productOption.getOptionName());
            modifyProductOption.setOptionPrice(productOption.getOptionPrice());
            modifyProductOption.setStock(productOption.getStock());
            modifyProductOption.setAmount(new Amount(productOption.getValue(), productOption.getUnit()));
            modifyProductOption.setProduct(modifyProduct);
            productOptionRepository.save(modifyProductOption);
        }
        return true;
    }

    // 상품 삭제
    @Override
    public boolean productDelete(ProductDeleteForm deleteForm) {
        final Admin admin = adminService.findAdminByUserToken(deleteForm.getUserToken());

        if(admin == null) {
            log.info("Can not find Admin");
            return false;
        }

        Optional<Product> maybeProduct = productRepository.findByIdWithFarm(deleteForm.getProductId());
        if(maybeProduct.isEmpty()) {
            log.info("Product is empty");
            return false;
        }
        Product deleteProduct = maybeProduct.get();

        Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProduct(deleteProduct);
        if(maybeProductMainImage.isEmpty()) {
            log.info("ProductMainImage is empty");
            return false;
        }
        ProductMainImage deleteProductMainImage = maybeProductMainImage.get();
        productMainImageRepository.delete(deleteProductMainImage);

        List<ProductDetailImages> deleteProductDetailImagesList = productDetailImagesRepository.findByProduct(deleteProduct);
        for(ProductDetailImages productDetailImages: deleteProductDetailImagesList) {
            productDetailImagesRepository.delete(productDetailImages);
        }

        List<ProductOption> deleteProductOptionList = productOptionRepository.findByProduct(deleteProduct);
        for(ProductOption productOption: deleteProductOptionList) {
            productOptionRepository.delete(productOption);
        }

        productRepository.delete(deleteProduct);
        return true;
    }

    @Override
    public boolean productListDelete(ProductListDeleteForm listDeleteForm) {
        final Admin admin = adminService.findAdminByUserToken(listDeleteForm.getUserToken());

        if(admin == null) {
            log.info("Can not find Admin");
            return false;
        }

        List<Product> productList = productRepository.findAllByIdWithFarm(listDeleteForm.getProductIdList());
        for(Product deleteProduct: productList) {
            Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProduct(deleteProduct);
            if(maybeProductMainImage.isEmpty()) {
                log.info("ProductMainImage is empty");
                return false;
            }
            ProductMainImage deleteProductMainImage = maybeProductMainImage.get();
            productMainImageRepository.delete(deleteProductMainImage);

            List<ProductDetailImages> deleteProductDetailImagesList = productDetailImagesRepository.findByProduct(deleteProduct);
            for(ProductDetailImages productDetailImages: deleteProductDetailImagesList) {
                productDetailImagesRepository.delete(productDetailImages);
            }

            List<ProductOption> deleteProductOptionList = productOptionRepository.findByProduct(deleteProduct);
            for(ProductOption productOption: deleteProductOptionList) {
                productOptionRepository.delete(productOption);
            }

            productRepository.delete(deleteProduct);
        }
        return true;
    }

    // 관리자용 상품 목록 조회
    @Override
    public List<AdminProductListResponseForm> getAdminProductList(String userToken) {
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if(admin == null) {
            log.info("Can not find Admin");
            return null;
        }

        // 최종적으로 관리자가 볼 상품 목록 form
        List<AdminProductListResponseForm> adminProductListResponseFormList = new ArrayList<>();

        // DB에서 상품 목록 조회
        List<Product> productList = productRepository.findAll();
        for(Product product: productList) {

            // 찾은 상품의 옵션 목록
            List<AdminProductOptionListResponse> adminProductOptionListResponseList = new ArrayList<>();

            // DB에서 상품 옵션 목록 조회 후 form에 담기
            List<ProductOption> productOptionList = productOptionRepository.findByProduct(product);
            for(ProductOption productOption: productOptionList) {
                AdminProductOptionListResponse adminProductOptionListResponse
                        = new AdminProductOptionListResponse(
                                productOption.getOptionName(),
                                productOption.getOptionPrice(),
                                productOption.getStock(),
                                productOption.getOptionSaleStatus());

                adminProductOptionListResponseList.add(adminProductOptionListResponse);
            }
            AdminProductListResponseForm adminProductListResponseForm
                    = new AdminProductListResponseForm(
                            product.getId(),
                            product.getProductName(),
                            product.getProductSaleStatus(),
                            adminProductOptionListResponseList);
            adminProductListResponseFormList.add(adminProductListResponseForm);
        }

        return adminProductListResponseFormList;
    }

    // 일반 사용자용 상품 목록 조회
    @Override
    public List<UserProductListResponseForm> getUserProductList() {
        List<UserProductListResponseForm> userProductListResponseFormList = new ArrayList<>();

        // DB에서 상품 목록 조회
        List<Product> productList = productRepository.findAllWithFarm();
        for(Product product: productList) {

            List<Long> optionPriceList = new ArrayList<>();
            Long minOptionPrice = 0L;
            int totalOptionStock = 0;
            Boolean isSoldOut = false;
            String productMainImage = null;

            // DB에서 상품 옵션 최저가 및 재고 조회
            List<ProductOption> productOptionList = productOptionRepository.findByProduct(product);
            for(ProductOption productOption: productOptionList) {

                optionPriceList.add(productOption.getOptionPrice());
                minOptionPrice = findMinValue(optionPriceList);

                totalOptionStock = totalOptionStock + productOption.getStock();
            }
            if(totalOptionStock == 0) {
                isSoldOut = true;
            }

            // DB에서 상품 메인 이미지 이름 조회
            Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProduct(product);
            if(maybeProductMainImage.isPresent()) {
                productMainImage = maybeProductMainImage.get().getMainImg();
            }

            // DB에서 해당 상품의 공급자(농가) 조회
            Farm farm = product.getFarm();
            FarmOperation farmOperation = farmOperationRepository.findByFarm(farm);

            // 최종적으로 반환할 상품 목록 form
            UserProductListResponseForm userProductListResponseForm
                    = new UserProductListResponseForm(
                    product.getId(),
                    product.getProductName(),
                    product.getCultivationMethod(),
                    productMainImage,
                    minOptionPrice,
                    isSoldOut,
                    farm.getFarmName(),
                    farm.getMainImage(),
                    farmOperation.getRepresentativeName());
            userProductListResponseFormList.add(userProductListResponseForm);
        }

        return userProductListResponseFormList;
    }

    // unit 구별 util
    public Unit unitDecision (String unit) {
        if (unit.equals("KG")) {
            return Unit.KG;
        } else if (unit.equals("G")) {
            return Unit.G;
        } else {
            return Unit.EA;
        }
    }

    // cultivation method 구별 util
    public CultivationMethod cultivationMethodDecision (String cultivationMethod) {
        if (cultivationMethod.equals("PESTICIDE_FREE")) {
            return CultivationMethod.PESTICIDE_FREE;
        } else if (cultivationMethod.equals("ENVIRONMENT_FRIENDLY")) {
            return CultivationMethod.ENVIRONMENT_FRIENDLY;
        } else {
            return CultivationMethod.ORGANIC;
        }
    }
}

package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.request.*;
import com.dyes.backend.domain.product.service.Response.ProductResponseForm;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.Response.ProductDetailImagesResponse;
import com.dyes.backend.domain.product.service.Response.ProductMainImageResponse;
import com.dyes.backend.domain.product.service.Response.ProductOptionResponse;
import com.dyes.backend.domain.product.service.Response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    final private ProductRepository productRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private ProductDetailImagesRepository productDetailImagesRepository;

    // 상품 등록
    @Override
    public boolean productRegistration(ProductRegisterForm registerForm) {
        log.info("productRegistration start");

        ProductRegisterRequest productRequest = registerForm.getProductRegisterRequest();
        List<ProductOptionRegisterRequest> productOptionRegisterRequests = registerForm.getProductOptionRegisterRequest();
        ProductMainImageRegisterRequest productMainImageRegisterRequest = registerForm.getProductMainImageRegisterRequest();
        List<ProductDetailImagesRegisterRequest> productDetailImagesRegisterRequests = registerForm.getProductDetailImagesRegisterRequests();

        try {
            Product product = Product.builder()
                    .productName(productRequest.getProductName())
                    .productDescription(productRequest.getProductDescription())
                    .cultivationMethod(cultivationMethodDecision(productRequest.getCultivationMethod()))
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
    public ProductResponseForm readProduct(Long productId) {
        log.info("readProduct start");
        try {
            Product product = productRepository.findById(productId).get();
            log.info("product: " + product);
            List<ProductOption> productOption = productOptionRepository.findByProduct(product);
            log.info("productOption: " + productOption);
            ProductMainImage productMainImage = productMainImageRepository.findByProduct(product).get();
            log.info("productMainImage: " + productMainImage);
            List<ProductDetailImages> productDetailImages = productDetailImagesRepository.findByProduct(product);
            log.info("productDetailImages: " + productDetailImages);

            ProductResponse productResponse = new ProductResponse().productResponse(product);
            List<ProductOptionResponse> productOptionResponse = new ProductOptionResponse().productOptionResponseList(productOption);
            ProductMainImageResponse productMainImageResponse = new ProductMainImageResponse().productMainImageResponse(productMainImage);
            List<ProductDetailImagesResponse> productDetailImagesResponses = new ProductDetailImagesResponse().productDetailImagesResponseList(productDetailImages);

            ProductResponseForm responseForm = new ProductResponseForm(productResponse, productOptionResponse, productMainImageResponse, productDetailImagesResponses);
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
    public boolean productModify(ProductModifyForm modifyForm) {
        ProductModifyRequest productModifyRequest = modifyForm.getProductModifyRequest();
        ProductMainImageModifyRequest productMainImageModifyRequest = modifyForm.getProductMainImageModifyRequest();
        List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequestList = modifyForm.getProductDetailImagesModifyRequest();
        List<ProductOptionModifyRequest> productOptionModifyRequestList = modifyForm.getProductOptionModifyRequest();

        // 상품 기본 정보 업데이트
        final Long modifyProductId = productModifyRequest.getProductId();
        Optional<Product> maybeProduct = productRepository.findById(modifyProductId);
        if(maybeProduct.isEmpty()) {
            log.info("Product is empty");
            return false;
        }

        Product modifyProduct = maybeProduct.get();
        modifyProduct.setProductName(productModifyRequest.getProductName());
        modifyProduct.setProductDescription(productModifyRequest.getProductDescription());
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

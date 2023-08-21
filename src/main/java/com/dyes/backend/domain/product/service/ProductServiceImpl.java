package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.request.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.request.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.request.ProductOptionRegisterRequest;
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
import com.dyes.backend.domain.product.service.request.ProductRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductDetailImagesRepository;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.product.service.request.ProductRegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    final private ProductRepository productRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private ProductDetailImagesRepository productDetailImagesRepository;

    @Override
    public boolean productRegistration(ProductRegisterForm registerForm) {
        ProductRegisterRequest request = registerForm.toProductRegister();
        try {
            Product product = Product.builder()
                    .productDescription(request.getProductDescription())
                    .cultivationMethod(cultivationMethodDecision(request.getCultivationMethod()))
                    .build();
            productRepository.save(product);

            for (int i = 0; i < request.getProductOptionRequest().size(); i++) {
                ProductOption productOption = ProductOption.builder()
                        .optionPrice(request.getProductOptionRequest().get(i).getOptionPrice())
                        .stock(request.getProductOptionRequest().get(i).getStock())
                        .optionName(request.getProductOptionRequest().get(i).getOptionName())
                        .amount(Amount.builder()
                                .value(request.getProductOptionRequest().get(i).getValue())
                                .unit(unitDecision(request.getProductOptionRequest().get(i).getUnit()))
                                .build())
                        .product(product)
                        .build();
                productOptionRepository.save(productOption);
            }

            ProductMainImage mainImage = ProductMainImage.builder()
                    .id(product.getId())
                    .mainImg(request.getMainImg())
                    .product(product)
                    .build();
            productMainImageRepository.save(mainImage);

            for (String detailImagesInRegisterForm : request.getDetailImgs()) {
                ProductDetailImages detailImages = ProductDetailImages.builder()
                        .detailImgs(detailImagesInRegisterForm)
                        .product(product)
                        .build();
                productDetailImagesRepository.save(detailImages);
            }
            return true;
        } catch (Exception e) {
            log.error("Can't register this product: {}", e.getMessage(), e);
            return false;
        }
    }
    public Unit unitDecision (String unit) {
        if (unit.equals("KG")) {
            return Unit.KG;
        } else if (unit.equals("G")) {
            return Unit.G;
        } else {
            return Unit.EA;
        }
    }
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

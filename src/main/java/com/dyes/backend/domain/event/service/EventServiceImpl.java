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
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.service.request.FarmAuthenticationRequest;
import com.dyes.backend.domain.product.controller.admin.form.ProductRegisterRequestForm;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.admin.request.register.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductOptionRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductRegisterRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

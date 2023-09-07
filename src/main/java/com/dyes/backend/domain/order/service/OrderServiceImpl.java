package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderConfirmResponseForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInProductPageRequestForm;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.OrderedPurchaserProfile;
import com.dyes.backend.domain.order.entity.OrderedPurchaserProfileAddress;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.order.repository.OrderedPurchaserProfileRepository;
import com.dyes.backend.domain.order.service.request.OrderConfirmRequest;
import com.dyes.backend.domain.order.service.request.OrderedProductOptionRequest;
import com.dyes.backend.domain.order.service.request.OrderedPurchaserProfileRequest;
import com.dyes.backend.domain.order.service.response.OrderConfirmProductResponse;
import com.dyes.backend.domain.order.service.response.OrderConfirmUserResponse;
import com.dyes.backend.domain.product.entity.ProductMainImage;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    final private OrderRepository orderRepository;
    final private CartRepository cartRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ContainProductOptionRepository containProductOptionRepository;
    final private UserProfileRepository userProfileRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private OrderedProductRepository orderedProductRepository;
    final private OrderedPurchaserProfileRepository orderedPurchaserProfileRepository;
    final private CartService cartService;
    final private AuthenticationService authenticationService;

    // 장바구니에서 상품 주문
    @Override
    public boolean orderProductInCart(OrderProductInCartRequestForm requestForm) {
        log.info("orderProductInCart start");
        try {

            OrderedPurchaserProfileRequest profileRequest = OrderedPurchaserProfileRequest.builder()
                    .orderedPurchaserName(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserName())
                    .orderedPurchaserContactNumber(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserContactNumber())
                    .orderedPurchaserEmail(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserEmail())
                    .orderedPurchaserAddress(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddress())
                    .orderedPurchaserZipCode(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserZipCode())
                    .orderedPurchaserAddressDetail(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddressDetail())
                    .build();
            final String paymentNumber = "랜덤한 결제 정보" + UUID.randomUUID();
            final String userToken = requestForm.getUserToken();
            final int totalAmount = requestForm.getTotalAmount();
            User user = authenticationService.findUserByUserToken(userToken);
            List<OrderedProductOptionRequest> orderedProductOptionRequestList= requestForm.getOrderedProductOptionRequestList();

            saveOrderedData(profileRequest, paymentNumber, totalAmount, user, orderedProductOptionRequestList);

            // 주문한 상품이 장바구니에 있으면 장바구니에서 목록 제거

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartService.cartCheckFromUserToken(userToken);

            // 장바구니에 담긴 상품 리스트 불러오기
            List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCart(cart);

            for(ContainProductOption containProductOption : productOptionList) {
                for (OrderedProductOptionRequest orderedProductOptionRequest : orderedProductOptionRequestList)
                    if (Objects.equals(containProductOption.getOptionId(), orderedProductOptionRequest.getProductOptionId())) {
                        containProductOptionRepository.delete(containProductOption);
                    }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while ordering products in cart", e);
            return false;
        }
    }

    // 제품 페이지에서 상품 주문
    @Override
    public boolean orderProductInProductPage(OrderProductInProductPageRequestForm requestForm) {
        log.info("orderProductInProductPage start");

        try {

            OrderedPurchaserProfileRequest profileRequest = OrderedPurchaserProfileRequest.builder()
                    .orderedPurchaserName(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserName())
                    .orderedPurchaserContactNumber(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserContactNumber())
                    .orderedPurchaserEmail(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserEmail())
                    .orderedPurchaserAddress(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddress())
                    .orderedPurchaserZipCode(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserZipCode())
                    .orderedPurchaserAddressDetail(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddressDetail())
                    .build();
            final String paymentNumber = "랜덤한 결제 정보" + UUID.randomUUID();
            final String userToken = requestForm.getUserToken();
            final int totalAmount = requestForm.getTotalAmount();
            User user = authenticationService.findUserByUserToken(userToken);
            List<OrderedProductOptionRequest> orderedProductOptionRequestList= requestForm.getOrderedProductOptionRequestList();

            saveOrderedData(profileRequest, paymentNumber, totalAmount, user, orderedProductOptionRequestList);

            log.info("orderProductInProductPage end");
            return true;
        } catch (Exception e) {
            log.error("Error occurred while ordering products in cart", e);
            return false;
        }
    }

    // 상품을 주문하기 전에 확인하기
    @Override
    public OrderConfirmResponseForm orderConfirm(OrderConfirmRequestForm requestForm) {
        try {
            OrderConfirmRequest request = new OrderConfirmRequest(requestForm.getUserToken());

            final String userToken = request.getUserToken();
            // 유저 정보 찾기
            User user = authenticationService.findUserByUserToken(userToken);
            UserProfile userProfile = userProfileRepository.findByUser(user).get();

            // 반환될 유저 정보
            OrderConfirmUserResponse userResponse = new OrderConfirmUserResponse();

            try {
                userResponse = OrderConfirmUserResponse.builder()
                        .email(userProfile.getEmail())
                        .contactNumber(userProfile.getContactNumber())
                        .address(userProfile.getAddress().getAddress())
                        .zipCode(userProfile.getAddress().getZipCode())
                        .addressDetail(userProfile.getAddress().getAddressDetail())
                        .build();
            } catch (NullPointerException e) {
                log.error("please set your user profile");
            }

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartRepository.findByUser(user).get();
            log.info("cart: " + cart.getId());

            // 장바구니에 담긴 상품 리스트 불러오기
            List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCart(cart);
            log.info("productOptionList: " + productOptionList.get(0).getProductName());

            // 장바구니에 담긴 물건이 없으면 에러
            if (productOptionList.size() == 0) {
                throw new IllegalArgumentException("No exist product in the cart");
            }
            // 장바구니에 담긴 물건을 모조리 불러오기
            List<OrderConfirmProductResponse> productResponseList = new ArrayList<>();
            for (ContainProductOption containProductOption : productOptionList) {
                ProductOption productOption = productOptionRepository.findByIdWithProduct(containProductOption.getOptionId()).get();
                log.info("productOption: " + productOption.getOptionName());

                ProductMainImage mainImage = productMainImageRepository.findByProductId(productOption.getProduct().getId()).get();
                log.info("mainImage: " + mainImage.getMainImg());

                // 반환될 상품 정보
                OrderConfirmProductResponse productResponse = OrderConfirmProductResponse.builder()
                        .productName(productOption.getProduct().getProductName())
                        .optionId(productOption.getId())
                        .optionPrice(productOption.getOptionPrice())
                        .optionCount(containProductOption.getOptionCount())
                        .productMainImage(mainImage.getMainImg())
                        .value(productOption.getAmount().getValue())
                        .unit(productOption.getAmount().getUnit())
                        .build();
                productResponseList.add(productResponse);
            }
            OrderConfirmResponseForm responseForm = new OrderConfirmResponseForm(userResponse, productResponseList);

            return responseForm;
        } catch (Exception e) {

            log.error("Error occurred while confirm products in cart", e);
            return null;
        }
    }

    public void saveOrderedData (OrderedPurchaserProfileRequest profileRequest, String paymentNumber,
                                 int totalAmount, User user, List<OrderedProductOptionRequest> orderedProductOptionRequestList) {

        final String purchaserName = profileRequest.getOrderedPurchaserName();
        final String purchaserContactNumber = profileRequest.getOrderedPurchaserContactNumber();
        final String purchaserEmail = profileRequest.getOrderedPurchaserEmail();
        final String purchaserAddress = profileRequest.getOrderedPurchaserAddress();
        final String purchaserZipCode = profileRequest.getOrderedPurchaserZipCode();
        final String purchaserAddressDetail = profileRequest.getOrderedPurchaserAddressDetail();

        // 주문 저장
        ProductOrder order = ProductOrder.builder()
                .id(paymentNumber)
                .user(user)
                .totalAmount(totalAmount)
                .orderedTime(LocalDate.now())
                .build();

        orderRepository.save(order);

        // 주문 상품 저장
        OrderedProduct orderedProduct;
        for(OrderedProductOptionRequest optionRequest : orderedProductOptionRequestList) {
            orderedProduct = OrderedProduct.builder()
                    .productOrder(order)
                    .productOptionId(optionRequest.getProductOptionId())
                    .productOptionCount(optionRequest.getProductOptionCount())
                    .build();

            orderedProductRepository.save(orderedProduct);
        }
        // 구매자 정보 저장
        OrderedPurchaserProfile purchaserProfile = OrderedPurchaserProfile.builder()
                .productOrder(order)
                .orderedPurchaseName(purchaserName)
                .orderedPurchaseContactNumber(purchaserContactNumber)
                .orderedPurchaseEmail(purchaserEmail)
                .orderedPurchaseProfileAddress(new OrderedPurchaserProfileAddress(purchaserAddress, purchaserZipCode, purchaserAddressDetail))
                .build();

        orderedPurchaserProfileRepository.save(purchaserProfile);
    }
}

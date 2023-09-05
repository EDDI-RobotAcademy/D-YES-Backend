package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.cart.service.request.ContainProductOptionRequest;
import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderConfirmResponseForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInProductPageRequestForm;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.service.request.OrderConfirmRequest;
import com.dyes.backend.domain.order.service.request.OrderProductInCartRequest;
import com.dyes.backend.domain.order.service.request.OrderProductInProductPageRequest;
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

import java.util.ArrayList;
import java.util.List;

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
    final private CartService cartService;
    final private AuthenticationService authenticationService;

    // 장바구니에서 상품 주문
    @Override
    public boolean orderProductInCart(OrderProductInCartRequestForm requestForm) {
        log.info("orderProductInCart start");
        try {
            OrderProductInCartRequest request = new OrderProductInCartRequest(requestForm.getUserToken());
            final String userToken = request.getUserToken();

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartService.cartCheckFromUserToken(userToken);

            // 장바구니에 담긴 상품 리스트 불러오기
            List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCart(cart);

            for(ContainProductOption containProductOption : productOptionList) {

                // 주문된 상품의 재고를 확인하기
                ProductOption productOption = productOptionRepository.findByIdWithProduct(containProductOption.getOptionId()).get();

                int stock = productOption.getStock();
                int stockCount = stock - containProductOption.getOptionCount();
                // 재고보다 주문수량이 많으면 exception
                if (stockCount < 0) {
                    throw new IllegalArgumentException("No stock the product option: " + productOption.getId());
                }

                // 불러온 상품 리스트를 주문으로 옮겨 담기
                ProductOrder order = ProductOrder.builder()
                        .userId(cart.getUser().getId())
                        .cartId(cart.getId())
                        .productOptionId(containProductOption.getOptionId())
                        .productOptionCount(containProductOption.getOptionCount())
                        .build();
                orderRepository.save(order);
                // 옮긴 상품은 장바구니에서 삭제하기
                containProductOptionRepository.delete(containProductOption);

                productOption.setStock(stockCount);
                productOptionRepository.save(productOption);
            }
            // 주문 끝난 장바구니를 삭제하기
            cartRepository.delete(cart);
            log.info("orderProductInCart end");
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
        final String userToken = requestForm.getUserToken();
        OrderProductInProductPageRequest request = new OrderProductInProductPageRequest(requestForm.getRequest().getProductOptionId(), requestForm.getRequest().getOptionCount());
        log.info("getProductOptionId: " + requestForm.getRequest().getProductOptionId());
        log.info("getProductCount: " + requestForm.getRequest().getOptionCount());

        try {
            // 상품을 카트에 넣기
            ContainProductOptionRequest containProductOptionRequest = new ContainProductOptionRequest(request.getProductOptionId(), request.getOptionCount());
            ContainProductRequestForm containProductRequestForm = new ContainProductRequestForm(userToken, containProductOptionRequest);
            cartService.containProductIntoCart(containProductRequestForm);

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartService.cartCheckFromUserToken(userToken);
            log.info("cart: " + cart.getId());

            // 장바구니에 담긴 상품 리스트 불러오기
            List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCart(cart);
            log.info("productOptionList: " + productOptionList.get(0).getProductName());


            for(ContainProductOption containProductOption : productOptionList) {
                // 주문된 상품의 재고를 확인하기
                ProductOption productOption = productOptionRepository.findByIdWithProduct(containProductOption.getId()).get();
                log.info("containProductOption: " + containProductOption.getId());

                log.info("productOption: " + productOption.getOptionName());

                int stock = productOption.getStock();
                log.info("stock: " + stock);
                int stockCount = stock - containProductOption.getOptionCount();
                log.info("getOptionCount: " + containProductOption.getOptionCount());
                log.info("stockCount: " + stockCount);

                // 재고보다 주문수량이 많으면 exception
                if (stockCount < 0) {
                    throw new IllegalArgumentException("No stock the product option: " + productOption.getId());
                }

                // 불러온 상품 리스트를 주문으로 옮겨 담기
                ProductOrder order = ProductOrder.builder()
                        .userId(cart.getUser().getId())
                        .cartId(cart.getId())
                        .productOptionId(containProductOption.getId())
                        .productOptionCount(containProductOption.getOptionCount())
                        .build();
                orderRepository.save(order);
                log.info("save order");
                // 옮긴 상품은 장바구니에서 삭제하기
                containProductOptionRepository.delete(containProductOption);
                log.info("delete ordered product option");

                productOption.setStock(stockCount);
                productOptionRepository.save(productOption);
                log.info("save changed product option");
            }
            // 주문 끝난 장바구니를 삭제하기
            log.info("delete cart");
            cartRepository.delete(cart);
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
}

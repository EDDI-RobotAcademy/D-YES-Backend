package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.service.request.OrderProductInCartRequest;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    final private OrderRepository orderRepository;
    final private CartRepository cartRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ContainProductOptionRepository containProductOptionRepository;
    final private CartService cartService;
    @Override
    public boolean orderProductInCart(OrderProductInCartRequestForm requestForm) {
        log.info("orderProductInCart start");
        try {
            OrderProductInCartRequest request = new OrderProductInCartRequest(requestForm.getUserToken());
            final String userToken = request.getUserToken();

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartService.cartCheckFromUserToken(userToken);
            // 장바구니에 담긴 상품 리스트 불러오기
            List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCartWithProduct(cart);

            for(ContainProductOption containProductOption : productOptionList) {

                // 주문된 상품의 재고를 확인하기
                ProductOption productOption = productOptionRepository.findByIdWithProduct(containProductOption.getProductOption().getId()).get();

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
                        .productOptionId(containProductOption.getProductOption().getId())
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
}

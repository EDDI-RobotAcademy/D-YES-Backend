package com.dyes.backend.domain.review.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.service.request.ReviewOrderedCheckRequest;
import com.dyes.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    final private AuthenticationService authenticationService;
    final private OrderRepository orderRepository;
    final private OrderedProductRepository orderedProductRepository;
    final private ProductRepository productRepository;
    public boolean beforeMakeReview(ReviewOrderedCheckRequestForm requestForm) {
        log.info("beforeMakeReview start");
        ReviewOrderedCheckRequest request = new ReviewOrderedCheckRequest(requestForm.getUserToken(), requestForm.getProductId());
        try {
            final String userToken = request.getUserToken();
            final Long productId = request.getProductId();

            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                return false;
            }
            log.info("user: " + user.getId());

            List<ProductOrder> orderList = orderRepository.findAllByUser(user);

            for(ProductOrder order : orderList) {
                log.info("order: " + order.getId());

                List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrder(order);
                for(OrderedProduct orderedProduct : orderedProductList) {
                    log.info("orderedProduct: " + orderedProduct.getId());
                    Long productIdInOrderedProduct = orderedProduct.getProductOptionId();

                    Product product = productRepository.findById(productIdInOrderedProduct).get();
                    log.info("product: " + product.getProductName());

                    if (product.getId().equals(productId)) {
                        log.info("beforeMakeReview end");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
        }
        return false;
    }
}

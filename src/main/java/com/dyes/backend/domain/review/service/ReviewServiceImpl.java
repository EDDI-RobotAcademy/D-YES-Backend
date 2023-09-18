package com.dyes.backend.domain.review.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.controller.form.ReviewRegisterRequestForm;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewContent;
import com.dyes.backend.domain.review.repository.ReviewContentRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.review.service.request.ReviewOrderedCheckRequest;
import com.dyes.backend.domain.review.service.request.ReviewRegisterRequest;
import com.dyes.backend.domain.review.service.response.ReviewRequestResponse;
import com.dyes.backend.domain.review.service.response.form.ReviewRequestResponseForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    final private AuthenticationService authenticationService;
    final private OrderRepository orderRepository;
    final private OrderedProductRepository orderedProductRepository;
    final private ProductRepository productRepository;
    final private ReviewRepository reviewRepository;
    final private ReviewContentRepository reviewContentRepository;
    final private UserProfileRepository userProfileRepository;
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
    public boolean registerReview(ReviewRegisterRequestForm requestForm) {
        log.info("registerReview start");

        ReviewRegisterRequest request = new ReviewRegisterRequest(requestForm.getUserToken(), requestForm.getProductId(),
                requestForm.getTitle(), requestForm.getContent());

        final String userToken = request.getUserToken();
        final Long productId = request.getProductId();
        final String title = request.getTitle();
        final String content = request.getContent();

        try {
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                return false;
            }
            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
            if (maybeUserProfile.isEmpty()){
                return false;
            }
            UserProfile userProfile = maybeUserProfile.get();
            Optional<Product> maybeProduct = productRepository.findById(productId);
            if (maybeProduct.isEmpty()) {
                return false;
            }
            Product product = maybeProduct.get();

            ReviewContent reviewContent = new ReviewContent();
            reviewContent.setReviewContent(content);
            reviewContentRepository.save(reviewContent);

            Review review = Review.builder()
                    .title(title)
                    .ReviewContent(reviewContent)
                    .user(user)
                    .userNickName(userProfile.getNickName())
                    .product(product)
                    .createDate(LocalDate.now())
                    .modifyDate(LocalDate.now())
                    .build();
            reviewRepository.save(review);
            log.info("registerReview end");

            return true;
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return false;
        }
    }
    public ReviewRequestResponseForm readReview(Long reviewId) {
        log.info("**************************** review: " + reviewId);

        Optional<Review> maybeReview = reviewRepository.findByIdWithContent(reviewId);
        try {
            if (maybeReview.isEmpty()) {
                log.info("**************************** empty: ");

                return null;
            }
            Review review = maybeReview.get();
            log.info("review: " + review.getTitle());
            ReviewRequestResponse response = ReviewRequestResponse.builder()
                    .title(review.getTitle())
                    .content(review.getReviewContent().getReviewContent())
                    .createDate(review.getCreateDate())
                    .modifyDate(review.getModifyDate())
                    .userNickName(review.getUserNickName())
                    .build();
            log.info("response: " + response);

            ReviewRequestResponseForm responseForm = ReviewRequestResponseForm.builder()
                    .title(response.getTitle())
                    .content(response.getContent())
                    .createDate(response.getCreateDate())
                    .modifyDate(response.getModifyDate())
                    .userNickName(response.getUserNickName())
                    .build();
            log.info("responseForm: " + responseForm);

            return responseForm;
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return null;
        }
    }

}

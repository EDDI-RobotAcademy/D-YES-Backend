package com.dyes.backend.domain.review.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.controller.form.ReviewRegisterRequestForm;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewImages;
import com.dyes.backend.domain.review.entity.ReviewRating;
import com.dyes.backend.domain.review.repository.ReviewImagesRepository;
import com.dyes.backend.domain.review.repository.ReviewRatingRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.review.service.request.ReviewImagesRegisterRequest;
import com.dyes.backend.domain.review.service.request.ReviewOrderedCheckRequest;
import com.dyes.backend.domain.review.service.request.ReviewRegisterRequest;
import com.dyes.backend.domain.review.service.response.ReviewRequestImagesResponse;
import com.dyes.backend.domain.review.service.response.ReviewRequestResponse;
import com.dyes.backend.domain.review.service.response.form.ReviewRequestResponseForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    final private UserProfileRepository userProfileRepository;
    final private ReviewImagesRepository reviewImagesRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ReviewRatingRepository reviewRatingRepository;
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

            List<ProductOrder> orderList = orderRepository.findAllByUser(user);
            for(ProductOrder order : orderList) {
                List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrder(order);
                for(OrderedProduct orderedProduct : orderedProductList) {
                    Long productIdInOrderedProduct = orderedProduct.getProductOptionId();
                    Product product = productRepository.findById(productIdInOrderedProduct).get();

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
    public boolean registerReview(ReviewRegisterRequest reviewRegisterRequest, List<ReviewImagesRegisterRequest> reviewImagesRegisterRequestList) {
        log.info("registerReview start");

        final String userToken = reviewRegisterRequest.getUserToken();
        final Long orderId = reviewRegisterRequest.getOrderId();
        final List<Long> productOptionIdList = reviewRegisterRequest.getProductOptionIdList();
        final String content = reviewRegisterRequest.getContent();
        final Integer rating = reviewRegisterRequest.getRating();

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

            Optional<ProductOrder> maybeOrder = orderRepository.findById(orderId);
            if (maybeOrder.isEmpty()) {
                return false;
            }
            ProductOrder order = maybeOrder.get();

            List<String> productOptionNameList = new ArrayList<>();
            ProductOption productOption = new ProductOption();
            for (Long productOptionId : productOptionIdList) {
                Optional<ProductOption> maybeProductOption = productOptionRepository.findByIdWithProduct(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    return false;
                }
                productOption = maybeProductOption.get();
                productOptionNameList.add(productOption.getOptionName());
            }


            Review review = Review.builder()
                    .user(user)
                    .productOrder(order)
                    .content(content)
                    .productName(productOption.getProduct().getProductName())
                    .optionNameList(productOptionNameList)
                    .userNickName(userProfile.getNickName())
                    .product(productOption.getProduct())
                    .reviewDate(LocalDate.now())
                    .purchaseDate(order.getOrderedTime())
                    .build();
            reviewRepository.save(review);

            ReviewRating reviewRating = ReviewRating.builder()
                    .rating(rating)
                    .review(review)
                    .product(productOption.getProduct())
                    .build();
            reviewRatingRepository.save(reviewRating);
            log.info("registerReview end");

            for (ReviewImagesRegisterRequest imagesRegisterRequest : reviewImagesRegisterRequestList) {
                ReviewImages images = ReviewImages.builder()
                        .img(imagesRegisterRequest.getReviewImages())
                        .review(review)
                        .build();
                reviewImagesRepository.save(images);
            }
            return true;
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return false;
        }
    }
    public List<ReviewRequestResponseForm> listReview(Long productId) {
        log.info("listReview start");

        List<ReviewRequestResponseForm> responseFormList = new ArrayList<>();

        Optional<Product> maybeProduct = productRepository.findById(productId);
        if (maybeProduct.isEmpty()){
            return null;
        }
        Product product = maybeProduct.get();

        try {
            List<Review> reviewList = reviewRepository.findAllByProduct(product);

            return reviewTransferDto(reviewList);
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return null;
        }
    }
    public List<ReviewRequestResponseForm> userListReview(String userToken) {
        log.info("userListReview start");
        try {
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                log.info("no exist user");
                return null;
            }
            List<Review> reviewList = reviewRepository.findAllByUserWithProductAndOrder(user);
            return reviewTransferDto(reviewList);
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return null;
        }
    }
    public List<ReviewRequestResponseForm> reviewTransferDto (List<Review> reviewList) {

        List<ReviewRequestResponseForm> responseFormList = new ArrayList<>();

        for (Review review : reviewList) {
            List<ReviewImages> imagesList = reviewImagesRepository.findAllByReview(review);
            Optional<ReviewRating> maybeReviewRating = reviewRatingRepository.findByReview(review);

            ReviewRating reviewRating = new ReviewRating();
            if (maybeReviewRating.isEmpty()) {
                reviewRating.setRating(0);
            } else {
                reviewRating = maybeReviewRating.get();
            }

            ReviewRequestResponse response = ReviewRequestResponse.builder()
                    .userNickName(review.getUserNickName())
                    .productName(review.getProductName())
                    .optionNameList(review.getOptionNameList())
                    .content(review.getContent())
                    .rating(reviewRating.getRating())
                    .createDate(review.getReviewDate())
                    .purchaseDate(review.getPurchaseDate())
                    .build();

            List<ReviewRequestImagesResponse> imagesResponseList = new ArrayList<>();
            for (ReviewImages reviewImages : imagesList) {
                ReviewRequestImagesResponse imagesResponse = ReviewRequestImagesResponse.builder()
                        .reviewImageId(reviewImages.getId())
                        .reviewImages(reviewImages.getImg())
                        .build();
                imagesResponseList.add(imagesResponse);
            }

            ReviewRequestResponseForm responseForm = ReviewRequestResponseForm.builder()
                    .reviewRequestResponse(response)
                    .imagesResponseList(imagesResponseList)
                    .build();

            responseFormList.add(responseForm);
        }
        log.info("listReview end");
        return responseFormList;
    }
}

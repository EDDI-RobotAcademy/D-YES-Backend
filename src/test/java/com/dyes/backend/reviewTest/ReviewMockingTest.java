package com.dyes.backend.reviewTest;

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
import com.dyes.backend.domain.review.service.ReviewServiceImpl;
import com.dyes.backend.domain.review.service.request.ReviewImagesRegisterRequest;
import com.dyes.backend.domain.review.service.request.ReviewOrderedCheckRequest;
import com.dyes.backend.domain.review.service.request.ReviewRegisterRequest;
import com.dyes.backend.domain.review.service.response.form.ReviewRequestResponseForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@SpringBootTest
public class ReviewMockingTest {
    @Mock
    private AuthenticationService mockAuthenticationService;
    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private OrderedProductRepository mockOrderedProductRepository;
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private ReviewRepository mockReviewRepository;
    @Mock
    private UserProfileRepository mockUserProfileRepository;
    @Mock
    private ReviewImagesRepository mockReviewImagesRepository;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private ReviewRatingRepository mockReviewRatingRepository;
    @InjectMocks
    private ReviewServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockService = new ReviewServiceImpl(
                mockAuthenticationService,
                mockOrderRepository,
                mockOrderedProductRepository,
                mockProductRepository,
                mockReviewRepository,
                mockUserProfileRepository,
                mockReviewImagesRepository,
                mockProductOptionRepository,
                mockReviewRatingRepository
        );
    }

    @Test
    @DisplayName("review mocking test: check user")
    public void 사용자가_리뷰를_남기기_전에_구매했던_제품인지_확인합니다 () {
        final String userToken = "유저 토큰";
        final Long productId = 1L;

        ReviewOrderedCheckRequestForm requestFrom = new ReviewOrderedCheckRequestForm(userToken, productId);
        ReviewOrderedCheckRequest request = new ReviewOrderedCheckRequest(requestFrom.getUserToken(), requestFrom.getProductId());

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);
        ProductOrder order = new ProductOrder();
        when(mockOrderRepository.findAllByUser(user)).thenReturn(List.of(order));
        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(1L);
        when(mockOrderedProductRepository.findAllByProductOrder(order)).thenReturn(List.of(orderedProduct));
        Product product = new Product();
        product.setId(1L);
        when(mockProductRepository.findById(orderedProduct.getProductOptionId())).thenReturn(Optional.of(product));

        boolean result = mockService.beforeMakeReview(requestFrom);
        assertTrue(result);
    }
    @Test
    @DisplayName("review mocking test: register review")
    public void 사용자가_리뷰를_등록합니다 () {
        final String userToken = "유저 토큰";
        final Long orderId = 1L;
        final Long productOptionId = 1L;
        final String content = "내용";
        final Integer rating = 1;
        ReviewImagesRegisterRequest imagesRegisterRequest = new ReviewImagesRegisterRequest();

        ReviewRegisterRequestForm requestForm = new ReviewRegisterRequestForm(userToken, orderId, productOptionId, content, rating, List.of(imagesRegisterRequest));
        ReviewRegisterRequest request = new ReviewRegisterRequest(
                requestForm.getUserToken(), requestForm.getOrderId(),
                requestForm.getProductOptionId(), requestForm.getContent(), requestForm.getRating()
        );

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);
        UserProfile userProfile = new UserProfile();
        when(mockUserProfileRepository.findByUser(user)).thenReturn(Optional.of(userProfile));
        ProductOrder order = new ProductOrder();
        when(mockOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Product product = new Product();
        ProductOption productOption = new ProductOption();
        productOption.setProduct(product);
        when(mockProductOptionRepository.findByIdWithProduct(productOptionId)).thenReturn(Optional.of(productOption));

        boolean result = mockService.registerReview(requestForm);
        assertTrue(result);
    }
    @Test
    @DisplayName("review mocking test: list review")
    public void 사용자가_리뷰를_읽을_수_있습니다 () {
        final Long productId = 1L;

        Product product = new Product();
        when(mockProductRepository.findById(productId)).thenReturn(Optional.of(product));
        Review review = new Review();
        when(mockReviewRepository.findAllByProduct(product)).thenReturn(List.of(review));
        ReviewImages reviewImages = new ReviewImages();
        when(mockReviewImagesRepository.findAllByReview(review)).thenReturn(List.of(reviewImages));
        ReviewRating reviewRating = new ReviewRating();
        when(mockReviewRatingRepository.findByReview(review)).thenReturn(Optional.of(reviewRating));

        List<ReviewRequestResponseForm> result = mockService.listReview(productId);
        assertTrue(result != null);
    }
}

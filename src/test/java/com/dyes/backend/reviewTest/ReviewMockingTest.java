package com.dyes.backend.reviewTest;

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
import com.dyes.backend.domain.review.entity.ReviewDetailImages;
import com.dyes.backend.domain.review.entity.ReviewMainImage;
import com.dyes.backend.domain.review.repository.ReviewContentRepository;
import com.dyes.backend.domain.review.repository.ReviewDetailImagesRepository;
import com.dyes.backend.domain.review.repository.ReviewMainImageRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.review.service.ReviewServiceImpl;
import com.dyes.backend.domain.review.service.request.ReviewDetailImagesRegisterRequest;
import com.dyes.backend.domain.review.service.request.ReviewMainImageRegisterRequest;
import com.dyes.backend.domain.review.service.request.ReviewOrderedCheckRequest;
import com.dyes.backend.domain.review.service.request.ReviewRegisterRequest;
import com.dyes.backend.domain.review.service.response.ReviewRequestResponse;
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

import java.time.LocalDate;
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
    private ReviewContentRepository mockReviewContentRepository;
    @Mock
    private UserProfileRepository mockUserProfileRepository;
    @Mock
    private ReviewMainImageRepository mockReviewMainImageRepository;
    @Mock
    private ReviewDetailImagesRepository mockReviewDetailImagesRepository;
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
                mockReviewContentRepository,
                mockUserProfileRepository,
                mockReviewMainImageRepository,
                mockReviewDetailImagesRepository
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
        final Long productId = 1L;
        final String title = "제목";
        final String content = "내용";

        ReviewRegisterRequestForm requestForm = new ReviewRegisterRequestForm(userToken, productId, title, content, new ReviewMainImageRegisterRequest(), List.of(new ReviewDetailImagesRegisterRequest()));
        ReviewRegisterRequest request = new ReviewRegisterRequest(requestForm.getUserToken(), requestForm.getProductId(), requestForm.getTitle(), requestForm.getContent());

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);
        UserProfile userProfile = new UserProfile();
        when(mockUserProfileRepository.findByUser(user)).thenReturn(Optional.of(userProfile));
        Product product = new Product();
        product.setId(1L);
        when(mockProductRepository.findById(productId)).thenReturn(Optional.of(product));
        ReviewContent reviewContent = new ReviewContent();
        reviewContent.setReviewContent(content);
        Review review = Review.builder()
                .title(title)
                .ReviewContent(reviewContent)
                .user(user)
                .product(product)
                .createDate(LocalDate.now())
                .build();

        boolean result = mockService.registerReview(requestForm);
        assertTrue(result);
    }
    @Test
    @DisplayName("review mocking test: read review")
    public void 사용자가_리뷰를_읽을_수_있습니다 () {
        final Long reviewId = 1L;
        final String title = "title";
        final String content = "content";
        final LocalDate createDate = LocalDate.now();
        final LocalDate modifyDate = LocalDate.now();
        final String nickName = "nickName";

        Review review = new Review();
        ReviewContent reviewContent = new ReviewContent();
        reviewContent.setReviewContent(content);
        review.setTitle(title);
        review.setReviewContent(reviewContent);
        review.setUserNickName(nickName);
        review.setCreateDate(createDate);
        review.setModifyDate(modifyDate);

        when(mockReviewRepository.findByIdWithContent(reviewId)).thenReturn(Optional.of(review));

        ReviewMainImage reviewMainImage = new ReviewMainImage();
        reviewMainImage.setMainImg("main image");
        when(mockReviewMainImageRepository.findByReview(review)).thenReturn(reviewMainImage);

        ReviewDetailImages reviewDetailImages = new ReviewDetailImages();
        reviewDetailImages.setDetailImgs("detail images");
        when(mockReviewDetailImagesRepository.findAllByReview(review)).thenReturn(List.of(reviewDetailImages));

        ReviewRequestResponseForm result = mockService.readReview(reviewId);
        assertTrue(result != null);
    }
}

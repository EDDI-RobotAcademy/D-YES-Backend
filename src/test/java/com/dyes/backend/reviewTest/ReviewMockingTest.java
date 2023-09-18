package com.dyes.backend.reviewTest;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.service.ReviewServiceImpl;
import com.dyes.backend.domain.review.service.request.ReviewOrderedCheckRequest;
import com.dyes.backend.domain.user.entity.User;
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

    @InjectMocks
    private ReviewServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockService = new ReviewServiceImpl(
                mockAuthenticationService,
                mockOrderRepository,
                mockOrderedProductRepository,
                mockProductRepository
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
}

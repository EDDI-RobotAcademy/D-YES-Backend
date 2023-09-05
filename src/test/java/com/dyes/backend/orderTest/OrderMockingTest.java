package com.dyes.backend.orderTest;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInProductPageRequestForm;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.service.OrderServiceImpl;
import com.dyes.backend.domain.order.service.request.OrderProductInCartRequest;
import com.dyes.backend.domain.order.service.request.OrderProductInProductPageRequest;
import com.dyes.backend.domain.product.entity.Amount;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.entity.SaleStatus;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.Active;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserType;
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
import static org.mockito.Mockito.*;

@SpringBootTest

public class OrderMockingTest {
    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private ContainProductOptionRepository mockContainProductOptionRepository;
    @Mock
    private CartService mockCartService;
    @Mock
    private CartRepository mockCartRepository;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private AuthenticationService mockAuthenticationService;
    @InjectMocks
    private OrderServiceImpl mockOrderService;
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockOrderService = new OrderServiceImpl(
                mockOrderRepository,
                mockCartRepository,
                mockProductOptionRepository,
                mockContainProductOptionRepository,
                mockCartService
        );
    }
    @Test
    @DisplayName("order mocking test: order product in cart")
    public void 사용자가_장바구니에서_물품을_주문합니다 () {
        final String userToken = "google 유저";

        OrderProductInCartRequestForm requestForm = new OrderProductInCartRequestForm(userToken);
        OrderProductInCartRequest request = new OrderProductInCartRequest(requestForm.getUserToken());

        User user = new User("1", "엑세스토큰", "리프래시 토큰", Active.YES, UserType.GOOGLE);
        when(mockAuthenticationService.findUserByUserToken(request.getUserToken())).thenReturn(user);
        Cart cart = new Cart(1L, user);
        when(mockCartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        ProductOption productOption = new ProductOption(1L, "옵션이름", 2000L, 10, new Amount(), new Product(), SaleStatus.AVAILABLE);
        ContainProductOption containProductOption = new ContainProductOption(1L, cart, "상품명", "메인이미지", 1L, "옵션이름", 2000L, 1);
        when(mockContainProductOptionRepository.findAllByCart(cart)).thenReturn(List.of(containProductOption));

        ProductOrder order = new ProductOrder(1L, user.getId(), cart.getId(), productOption.getId(), containProductOption.getOptionCount());

        boolean actual = mockOrderService.orderProductInCart(requestForm);
        assertTrue(actual);
    }
    @Test
    @DisplayName("order mocking test: order product in product page")
    public void 사용자가_상품_페이지에서_물품을_주문합니다 () {
        final String userToken = "google 유저";
        final Long productOptionId = 1L;
        final int productCount = 1;

        OrderProductInProductPageRequest request = new OrderProductInProductPageRequest(productOptionId, productCount);
        OrderProductInProductPageRequestForm requestForm = new OrderProductInProductPageRequestForm(userToken, request);

        User user = new User("1", "엑세스토큰", "리프래시 토큰", Active.YES, UserType.GOOGLE);
        when(mockAuthenticationService.findUserByUserToken(requestForm.getUserToken())).thenReturn(user);

        Cart cart = new Cart(1L, user);
        when(mockCartService.cartCheckFromUserToken(userToken)).thenReturn(cart);

        ProductOption productOption = new ProductOption();
        ContainProductOption containProductOption = new ContainProductOption();
        when(mockContainProductOptionRepository.findAllByCart(cart)).thenReturn(List.of(containProductOption));
        when(mockProductOptionRepository.findByIdWithProduct(containProductOption.getId())).thenReturn(Optional.of(productOption));

        boolean actual = mockOrderService.orderProductInProductPage(requestForm);
        assertTrue(actual);
    }
}

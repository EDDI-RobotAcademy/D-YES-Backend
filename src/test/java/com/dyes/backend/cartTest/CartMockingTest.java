package com.dyes.backend.cartTest;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartServiceImpl;
import com.dyes.backend.domain.cart.service.request.ContainProductOptionRequest;
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

import static org.mockito.Mockito.*;

@SpringBootTest

public class CartMockingTest {
    @Mock
    private CartRepository mockCartRepository;
    @Mock
    private AuthenticationService mockAuthenticationService;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private ContainProductOptionRepository mockContainProductOptionRepository;
    @InjectMocks
    private CartServiceImpl mockCartService;
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockCartService = new CartServiceImpl(
                mockCartRepository,
                mockProductOptionRepository,
                mockContainProductOptionRepository,
                mockAuthenticationService
        );
    }
    @Test
    @DisplayName("cart mocking test: product contain into cart")
    public void 사용자가_장바구니에_물품을_담습니다 () {
        final String userToken = "google유저";
        final Long optionId = 1L;
        final int optionCount = 1;
        ContainProductOptionRequest request = new ContainProductOptionRequest(optionId, optionCount);
        ContainProductRequestForm requestForm = new ContainProductRequestForm(userToken, request);

        User user = new User("1", "엑세스토큰", "리프래시 토큰", Active.YES, UserType.GOOGLE);
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        when(mockCartRepository.findByUser(user)).thenReturn(Optional.empty());

        Cart cart = Cart.builder()
                .user(user)
                .build();

        ProductOption productOption = new ProductOption(1L, "옵션이름", 2000L, 10, new Amount(), new Product(), SaleStatus.AVAILABLE);

        when(mockProductOptionRepository.findById(request.getProductOptionId())).thenReturn(Optional.of(productOption));
        when(mockContainProductOptionRepository.findAllByCart(cart)).thenReturn(anyList());

        mockCartService.containProductIntoCart(requestForm);
        verify(mockCartRepository, times(1)).save(any());
        verify(mockContainProductOptionRepository, times(1)).save(any());
    }
}

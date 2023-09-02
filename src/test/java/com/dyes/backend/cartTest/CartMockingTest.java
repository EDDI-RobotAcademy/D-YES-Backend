package com.dyes.backend.cartTest;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.controller.form.ContainProductDeleteRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductModifyRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartServiceImpl;
import com.dyes.backend.domain.cart.service.request.ContainProductDeleteRequest;
import com.dyes.backend.domain.cart.service.request.ContainProductModifyRequest;
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
    @Test
    @DisplayName("cart mocking test: product count modify in cart")
    public void 사용자가_장바구니에_담긴_상품의_수량을_조절합니다 () {
        final String userToken = "google유저";
        final Long optionId = 1L;
        final int optionCount = 3;

        ContainProductModifyRequest request = new ContainProductModifyRequest(optionId, optionCount);
        ContainProductModifyRequestForm requestForm = new ContainProductModifyRequestForm(userToken, request);

        User user = new User("1", "엑세스토큰", "리프래시 토큰", Active.YES, UserType.GOOGLE);
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        Cart cart = new Cart(1L, user);
        when(mockCartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        ProductOption productOption = new ProductOption(1L, "옵션이름", 2000L, 10, new Amount(), new Product(), SaleStatus.AVAILABLE);
        ContainProductOption containProductOption = new ContainProductOption(1L, cart, productOption, 1);
        when(mockContainProductOptionRepository.findAllByCart(cart)).thenReturn(List.of(containProductOption));

        mockCartService.changeProductOptionCount(requestForm);
        verify(mockContainProductOptionRepository, times(1)).save(containProductOption);
    }
    @Test
    @DisplayName("cart mocking test: product delete in cart")
    public void 사용자가_장바구니에_담긴_상품을_삭제합니다 () {
        final String userToken = "google유저";
        final Long optionId = 1L;

        ContainProductDeleteRequestForm requestForm = new ContainProductDeleteRequestForm(userToken, optionId);
        ContainProductDeleteRequest request = new ContainProductDeleteRequest(requestForm.getUserToken(), requestForm.getProductOptionId());

        User user = new User("1", "엑세스토큰", "리프래시 토큰", Active.YES, UserType.GOOGLE);
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        Cart cart = new Cart(1L, user);
        when(mockCartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        ProductOption productOption = new ProductOption(1L, "옵션이름", 2000L, 10, new Amount(), new Product(), SaleStatus.AVAILABLE);
        ContainProductOption containProductOption = new ContainProductOption(1L, cart, productOption, 1);
        when(mockContainProductOptionRepository.findAllByCart(cart)).thenReturn(List.of(containProductOption));

        mockCartService.deleteProductOptionInCart(requestForm);
        verify(mockContainProductOptionRepository, times(1)).delete(containProductOption);
    }
}

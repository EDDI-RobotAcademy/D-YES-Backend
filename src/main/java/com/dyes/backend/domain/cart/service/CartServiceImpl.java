package com.dyes.backend.domain.cart.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.request.CartCheckFromUserTokenRequest;
import com.dyes.backend.domain.cart.service.request.ContainProductOptionRequest;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    final private CartRepository cartRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ContainProductOptionRepository containProductOptionRepository;
    final private AuthenticationService authenticationService;
    public void containProductIntoCart(ContainProductRequestForm requestForm) throws NullPointerException {
        log.info("containProductIntoCart start");
        // 유저가 장바구니가 있는지 확인
        log.info("ContainProductRequestForm: " + requestForm);

        CartCheckFromUserTokenRequest tokenRequest = new CartCheckFromUserTokenRequest(requestForm.getUserToken());
        Cart cart = cartCheckFromUserToken(tokenRequest.getUserToken());

        // 옵션 DTO에서 받아온 옵션 아이디와 옵션 갯수 넣기
        ContainProductOptionRequest request = new ContainProductOptionRequest(requestForm.getRequest().getProductOptionId(), requestForm.getRequest().getOptionCount());

        log.info("ContainProductOptionRequest: " + request);
        // 받아온 옵션이 DB에 있는 옵션인지 확인
        ProductOption productOption = isReallyExistProductOption(request.getProductOptionId());

        // 받아온 옵션이 카트에 담긴 옵션인지 확인
        ContainProductOption checkProductOptionInCart = checkProductOptionInCart(cart, request);

        // 카트에 담긴 옵션이 없다면 새로 옵션을 저장하기
        if (checkProductOptionInCart == null) {
            log.info("savedOptionList isEmpty");
            ContainProductOption containProductOption = ContainProductOption.builder()
                    .productOption(productOption)
                    .cart(cart)
                    .optionCount(request.getOptionCount())
                    .build();
            containProductOptionRepository.save(containProductOption);
            log.info("containProductIntoCart end");
        }
    }

    // 유저 토큰으로 카트가 있나 없나 확인하기
    public Cart cartCheckFromUserToken(String userToken) {
        User user = authenticationService.findUserByUserToken(userToken);
        log.info("user: " + user);

        Optional<Cart> maybeCart = cartRepository.findByUser(user);

        if (maybeCart.isEmpty()) {
            // 없으면 생성
            log.info("maybeCart isEmpty");
            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
            log.info("save new cart");
            return cart;
        } else {
            // 있으면 가져오기
            Cart cart = maybeCart.get();
            log.info("cart: " + cart);
            return cart;
        }
    }
    // 옵션이 DB에 있는 진짜 옵션인지 파악하기
    public ProductOption isReallyExistProductOption(Long productOptionId) {
        log.info("isReallyExistProductOption start");

        Optional<ProductOption> maybeOption = productOptionRepository.findByIdWithProductAndFarm(productOptionId);

        if (maybeOption.isEmpty()) {
            log.info("this option doesn't exist");
            log.info("isReallyExistProductOption end");
            return null;
        }
        // 있는 옵션이라면
        ProductOption productOption = maybeOption.get();
        log.info("ProductOption: " + productOption);
        log.info("isReallyExistProductOption end");
        return productOption;
    }
    // 카트에서 동일한 아이디가 있는지 확인하기
    public ContainProductOption checkProductOptionInCart(Cart cart, ContainProductOptionRequest request) {
        // 카트에 담긴 옵션들 다 불러오기
        List<ContainProductOption> savedOptionList = containProductOptionRepository.findAllByCart(cart);
        // 카트에 담긴 옵션 중에서 받아온 옵션과 동일한 옵션이 있는지 파악하기
        Optional<ContainProductOption> result = savedOptionList.stream()
                                                                .filter(option -> option.getId().equals(request.getProductOptionId()))
                                                                .findFirst();

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
}

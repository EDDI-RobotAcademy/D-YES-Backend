package com.dyes.backend.domain.cart.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.controller.form.ContainProductDeleteRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductListRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductModifyRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.reponse.ContainProductCountChangeResponse;
import com.dyes.backend.domain.cart.service.reponse.ContainProductListResponse;
import com.dyes.backend.domain.cart.service.request.*;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductMainImage;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    final private ProductMainImageRepository productMainImageRepository;
    final private AuthenticationService authenticationService;

    // 장바구니 상품 담기
    @Override
    public void containProductIntoCart(ContainProductRequestForm requestForm) throws NullPointerException {
        log.info("containProductIntoCart start");
        log.info("ContainProductRequestForm: " + requestForm);

        CartCheckFromUserTokenRequest tokenRequest = new CartCheckFromUserTokenRequest(requestForm.getUserToken());

        final String userToken = tokenRequest.getUserToken();

        // 유저토큰으로 장바구니 불러오기
        Cart cart = cartCheckFromUserToken(userToken);

        List<ContainProductOptionRequest> requestList = requestForm.getRequestList();

        for (ContainProductOptionRequest request : requestList){

            log.info("ContainProductOptionRequest: " + request);

            final Long requestProductOptionId = request.getProductOptionId();
            final int requestProductOptionCount = request.getOptionCount();

            // 받아온 옵션이 DB에 있는 옵션인지 확인
            ProductOption productOption = isReallyExistProductOption(requestProductOptionId);
            Product product = productOption.getProduct();
            final String productName = product.getProductName();
            final Long productId = product.getId();
            Optional<ProductMainImage> maybeProductMainImage = productMainImageRepository.findByProduct(product);
            String productMainImage = "";
            if(maybeProductMainImage.isPresent()) {
                productMainImage = maybeProductMainImage.get().getMainImg();
            }

            // 받아온 옵션이 카트에 담긴 옵션인지 확인
            ContainProductOption checkProductOptionInCart = checkProductOptionInCart(cart, requestProductOptionId);

            // 카트에 담긴 옵션이 없다면 새로 옵션을 저장하기
            if (checkProductOptionInCart == null) {
                log.info("savedOptionList isEmpty");
                ContainProductOption containProductOption = ContainProductOption.builder()
                        .cart(cart)
                        .productName(productName)
                        .productId(productId)
                        .productMainImage(productMainImage)
                        .optionId(productOption.getId())
                        .optionName(productOption.getOptionName())
                        .optionPrice(productOption.getOptionPrice())
                        .optionCount(requestProductOptionCount)
                        .build();
                containProductOptionRepository.save(containProductOption);
                log.info("containProductIntoCart end");
            } else {
                ContainProductOption containProductOption = checkProductOptionInCart(cart, requestProductOptionId);
                // 카트에 담긴 옵션 카운트를 바꾸기
                int changeCount = containProductOption.getOptionCount() + requestProductOptionCount;
                containProductOption.setOptionCount(changeCount);
                containProductOptionRepository.save(containProductOption);
            }
        }
    }

    // 장바구니 상품 수량 변경
    @Override
    public ContainProductCountChangeResponse changeProductOptionCount(ContainProductModifyRequestForm requestForm) throws NullPointerException{
        log.info("changeProductOptionCount start");

        CartCheckFromUserTokenRequest tokenRequest = new CartCheckFromUserTokenRequest(requestForm.getUserToken());
        ContainProductModifyRequest modifyRequest = new ContainProductModifyRequest(requestForm.getRequest().getProductOptionId(), requestForm.getRequest().getOptionCount());

        final String userToken = tokenRequest.getUserToken();
        final Long requestProductOptionId = modifyRequest.getProductOptionId();
        final int requestProductOptionCount = modifyRequest.getOptionCount();

        // 유저토큰으로 카트 불러오기
        Cart cart = cartCheckFromUserToken(userToken);

        // 카트와 옵션 id로 카트에 담긴 옵션 불러오기
        ContainProductOption containProductOption = checkProductOptionInCart(cart, requestProductOptionId);

        // 카트에 담긴 옵션 카운트를 바꾸기
        containProductOption.setOptionCount(requestProductOptionCount);
        containProductOptionRepository.save(containProductOption);

        ContainProductCountChangeResponse response = new ContainProductCountChangeResponse(requestProductOptionCount);
        log.info("changeProductOptionCount end");
        return response;
    }

    // 장바구니 상품 삭제
    @Override
    public void deleteProductOptionInCart(List<ContainProductDeleteRequestForm> requestFormList) {
        log.info("deleteProductOptionInCart start");
        for (ContainProductDeleteRequestForm requestForm : requestFormList) {
            ContainProductDeleteRequest deleteRequest = new ContainProductDeleteRequest(requestForm.getUserToken(), requestForm.getProductOptionId());

            final String userToken = deleteRequest.getUserToken();
            final Long requestProductOptionId = deleteRequest.getProductOptionId();

            // 유저토큰으로 카트 불러오기
            Cart cart = cartCheckFromUserToken(userToken);

            // 카트와 옵션 id로 카트에 담긴 옵션 불러오기
            ContainProductOption containProductOption = checkProductOptionInCart(cart, requestProductOptionId);

            containProductOptionRepository.delete(containProductOption);
        }
        log.info("deleteProductOptionInCart end");
    }

    // 장바구니 목록 조회
    @Override
    public List<ContainProductListResponse> productListResponse(ContainProductListRequestForm requestForm) {
        log.info("productListResponse start");
        ContainProductListRequest listRequest = new ContainProductListRequest(requestForm.getUserToken());

        final String userToken = listRequest.getUserToken();

        // 유저토큰으로 카트 불러오기
        Cart cart = cartCheckFromUserToken(userToken);

        // 카트에 담긴 옵션들 다 불러오기
        List<ContainProductOption> savedOptionList = containProductOptionRepository.findAllByCart(cart);

        List<ContainProductListResponse> responseList = new ArrayList<>();

        for (ContainProductOption containProductOption : savedOptionList) {

            if(containProductOption.getOptionId() == 0) {
                responseList.add(new ContainProductListResponse(
                        containProductOption.getProductName(),
                        containProductOption.getProductId(),
                        containProductOption.getProductMainImage(),
                        containProductOption.getOptionId(),
                        containProductOption.getOptionName(),
                        containProductOption.getOptionPrice(),
                        containProductOption.getOptionCount()));
            }
            if(containProductOption.getOptionId() != 0) {
                final Long productOptionId = containProductOption.getOptionId();
                final ProductOption productOption = productOptionRepository.findByIdWithProduct(productOptionId).get();
                final Product product = productOption.getProduct();
                final ProductMainImage productMainImage = productMainImageRepository.findByProductId(product.getId()).get();

                ContainProductListResponse response = ContainProductListResponse.builder()
                        .productName(product.getProductName())
                        .productId(product.getId())
                        .productMainImage(productMainImage.getMainImg())
                        .optionId(productOption.getId())
                        .optionName(productOption.getOptionName())
                        .optionPrice(productOption.getOptionPrice())
                        .optionCount(containProductOption.getOptionCount())
                        .build();

                responseList.add(response);
            }
        }
        log.info("productListResponse end");

        return responseList;
    }

    // 유저 토큰으로 사용자의 카트 유무 확인
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

    // 장바구니에 담은 옵션이 상품 옵션 DB에 존재하는지 확인
    public ProductOption isReallyExistProductOption(Long productOptionId) {
        log.info("isReallyExistProductOption start");

        Optional<ProductOption> maybeOption = productOptionRepository.findByIdWithProduct(productOptionId);

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

    // 사용자 카트에 동일한 옵션이 담겨있는지 확인
    public ContainProductOption checkProductOptionInCart(Cart cart, Long productOptionId) {
        // 카트에 담긴 옵션들 다 불러오기
        List<ContainProductOption> savedOptionList = containProductOptionRepository.findAllByCart(cart);

        // 카트에 담긴 옵션 중에서 받아온 옵션과 동일한 옵션이 있는지 파악하기
        Optional<ContainProductOption> result
                = savedOptionList.stream()
                                .filter(option -> option.getOptionId().equals(productOptionId))
                                .findFirst();

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

}

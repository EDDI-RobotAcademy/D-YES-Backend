package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductRequestForm;
import com.dyes.backend.domain.order.entity.DeliveryStatus;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.OrderedPurchaserProfile;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.order.repository.OrderedPurchaserProfileRepository;
import com.dyes.backend.domain.order.service.admin.response.OrderDetailInfoResponse;
import com.dyes.backend.domain.order.service.admin.response.OrderProductListResponse;
import com.dyes.backend.domain.order.service.admin.response.OrderUserInfoResponse;
import com.dyes.backend.domain.order.service.admin.response.form.OrderListResponseFormForAdmin;
import com.dyes.backend.domain.order.service.user.request.OrderConfirmRequest;
import com.dyes.backend.domain.order.service.user.request.OrderedProductOptionRequest;
import com.dyes.backend.domain.order.service.user.request.OrderedPurchaserProfileRequest;
import com.dyes.backend.domain.order.service.user.request.PaymentTemporarySaveRequest;
import com.dyes.backend.domain.order.service.user.response.OrderConfirmProductResponse;
import com.dyes.backend.domain.order.service.user.response.OrderConfirmUserResponse;
import com.dyes.backend.domain.order.service.user.response.OrderOptionListResponse;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.user.response.form.OrderListResponseFormForUser;
import com.dyes.backend.domain.payment.entity.Payment;
import com.dyes.backend.domain.payment.entity.PaymentAmount;
import com.dyes.backend.domain.payment.repository.PaymentRepository;
import com.dyes.backend.domain.payment.service.PaymentService;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRequest;
import com.dyes.backend.domain.payment.service.response.KakaoPaymentReadyResponse;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductMainImage;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.Address;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.*;

import static com.dyes.backend.domain.order.entity.DeliveryStatus.PREPARING;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    final private OrderRepository orderRepository;
    final private CartRepository cartRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ContainProductOptionRepository containProductOptionRepository;
    final private UserProfileRepository userProfileRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private OrderedProductRepository orderedProductRepository;
    final private OrderedPurchaserProfileRepository orderedPurchaserProfileRepository;
    final private PaymentRepository paymentRepository;
    final private CartService cartService;
    final private PaymentService paymentService;
    final private RedisService redisService;
    final private UserRepository userRepository;
    final private AuthenticationService authenticationService;

    public RedirectView purchaseReadyWithKakao(OrderProductRequestForm requestForm) {
        log.info("purchaseKakao start");

        User user = authenticationService.findUserByUserToken(requestForm.getUserToken());

        PaymentTemporarySaveRequest saveRequest = PaymentTemporarySaveRequest.builder()
                .userToken(requestForm.getUserToken())
                .orderedPurchaserProfileRequest(requestForm.getOrderedPurchaserProfileRequest())
                .orderedProductOptionRequestList(requestForm.getOrderedProductOptionRequestList())
                .totalAmount(requestForm.getTotalAmount())
                .from(requestForm.getFrom())
                .build();

        String itemName = "";
        Integer quantity = 0;

        for (OrderedProductOptionRequest optionRequest : saveRequest.getOrderedProductOptionRequestList()) {

            ProductOption productOption = productOptionRepository.findById(optionRequest.getProductOptionId()).get();

            itemName += productOption.getOptionName() + ", ";
            quantity += optionRequest.getProductOptionCount();
        }
        log.info("itemName: " + itemName);
        log.info("quantity: " + quantity);

        KakaoPaymentRequest request = KakaoPaymentRequest.builder()
                .partner_order_id(user.getId() + itemName)
                .partner_user_id(user.getId())
                .item_name(itemName)
                .quantity(quantity)
                .total_amount(saveRequest.getTotalAmount())
                .tax_free_amount(saveRequest.getTotalAmount()/11) // 세금을 10퍼센트라고 했을 때
                .build();
        log.info("request: " + request);

        KakaoPaymentReadyResponse response = paymentService.paymentRequest(request);
        log.info("response: " + response);


        Payment payment = Payment.builder()
                .tid(response.getTid())
                .partner_order_id(user.getId() + ": " + itemName)
                .partner_user_id(user.getId())
                .paymentAmount(new PaymentAmount(saveRequest.getTotalAmount(),
                        saveRequest.getTotalAmount()*1.1,
                        (saveRequest.getTotalAmount()*1.1)*10,
                        0,0))
                .item_name(itemName)
                .quantity(quantity)
                .approved_at(response.getCreated_at())
                .build();

        paymentRepository.save(payment);
        log.info("payment: " + payment);

        saveRequest.setTid(response.getTid());
        redisService.paymentTemporaryStorage(user.getId(), saveRequest);
        log.info("saveRequest: " + saveRequest);

        return new RedirectView(response.getNext_redirect_pc_url());
    }
    // 상품 주문
    public boolean orderProductInCart(OrderProductRequestForm requestForm) {
        log.info("orderProductInCart start");
        try {
            OrderedPurchaserProfileRequest profileRequest = OrderedPurchaserProfileRequest.builder()
                    .orderedPurchaserName(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserName())
                    .orderedPurchaserContactNumber(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserContactNumber())
                    .orderedPurchaserEmail(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserEmail())
                    .orderedPurchaserAddress(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddress())
                    .orderedPurchaserZipCode(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserZipCode())
                    .orderedPurchaserAddressDetail(requestForm.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddressDetail())
                    .build();

            final String userToken = requestForm.getUserToken();
            final int totalAmount = requestForm.getTotalAmount();
            User user = authenticationService.findUserByUserToken(userToken);
            List<OrderedProductOptionRequest> orderedProductOptionRequestList = requestForm.getOrderedProductOptionRequestList();

            saveOrderedData(profileRequest, totalAmount, user, orderedProductOptionRequestList);

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartService.cartCheckFromUserToken(userToken);

            // 주문한 상품이 장바구니에 있으면 장바구니에서 목록 제거
            if (requestForm.getFrom().equals("cart")) {
                // 장바구니에 담긴 상품 리스트 불러오기
                List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCart(cart);

                for (ContainProductOption containProductOption : productOptionList) {
                    for (OrderedProductOptionRequest orderedProductOptionRequest : orderedProductOptionRequestList)
                        if (Objects.equals(containProductOption.getOptionId(), orderedProductOptionRequest.getProductOptionId())) {
                            containProductOptionRepository.delete(containProductOption);
                        }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while ordering products in cart", e);
            return false;
        }
    }
    // 상품을 주문하기 전에 확인하기
    @Override
    public OrderConfirmResponseFormForUser orderConfirm(OrderConfirmRequestForm requestForm) {
        try {
            OrderConfirmRequest request = new OrderConfirmRequest(requestForm.getUserToken());

            final String userToken = request.getUserToken();
            // 유저 정보 찾기
            User user = authenticationService.findUserByUserToken(userToken);
            UserProfile userProfile = userProfileRepository.findByUser(user).get();

            // 반환될 유저 정보
            OrderConfirmUserResponse userResponse = new OrderConfirmUserResponse();

            try {
                userResponse = OrderConfirmUserResponse.builder()
                        .email(userProfile.getEmail())
                        .contactNumber(userProfile.getContactNumber())
                        .address(userProfile.getAddress().getAddress())
                        .zipCode(userProfile.getAddress().getZipCode())
                        .addressDetail(userProfile.getAddress().getAddressDetail())
                        .build();
            } catch (NullPointerException e) {
                log.error("please set your user profile");
            }

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartRepository.findByUser(user).get();
            log.info("cart: " + cart.getId());

            // 장바구니에 담긴 상품 리스트 불러오기
            List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCart(cart);
            log.info("productOptionList: " + productOptionList.get(0).getProductName());

            // 장바구니에 담긴 물건이 없으면 에러
            if (productOptionList.size() == 0) {
                throw new IllegalArgumentException("No exist product in the cart");
            }
            // 장바구니에 담긴 물건을 모조리 불러오기
            List<OrderConfirmProductResponse> productResponseList = new ArrayList<>();
            for (ContainProductOption containProductOption : productOptionList) {
                ProductOption productOption = productOptionRepository.findByIdWithProduct(containProductOption.getOptionId()).get();
                log.info("productOption: " + productOption.getOptionName());

                ProductMainImage mainImage = productMainImageRepository.findByProductId(productOption.getProduct().getId()).get();
                log.info("mainImage: " + mainImage.getMainImg());

                // 반환될 상품 정보
                OrderConfirmProductResponse productResponse = OrderConfirmProductResponse.builder()
                        .productName(productOption.getProduct().getProductName())
                        .optionId(productOption.getId())
                        .optionPrice(productOption.getOptionPrice())
                        .optionCount(containProductOption.getOptionCount())
                        .productMainImage(mainImage.getMainImg())
                        .value(productOption.getAmount().getValue())
                        .unit(productOption.getAmount().getUnit())
                        .build();
                productResponseList.add(productResponse);
            }
            OrderConfirmResponseFormForUser responseForm = new OrderConfirmResponseFormForUser(userResponse, productResponseList);

            return responseForm;
        } catch (Exception e) {

            log.error("Error occurred while confirm products in cart", e);
            return null;
        }
    }

    // 관리자의 주문 내역 확인
    @Override
    public List<OrderListResponseFormForAdmin> getOrderListForAdmin() {

        // 모든 주문 내역 가져오기
        List<ProductOrder> orderList = orderRepository.findAllWithUser();

        List<OrderListResponseFormForAdmin> orderListResponseFormForAdmins = new ArrayList<>();

        for (ProductOrder order : orderList) {

            // 주문자 정보 가져오기
            User user = order.getUser();
            String userId = user.getId();

            Optional<OrderedPurchaserProfile> maybeOrderedPurchaserProfile = orderedPurchaserProfileRepository.findByProductOrder(order);
            if (maybeOrderedPurchaserProfile.isEmpty()) {
                log.info("Profile with order ID '{}' not found", order.getId());
                return null;
            }

            OrderedPurchaserProfile purchaserProfile = maybeOrderedPurchaserProfile.get();
            String contactNumber = purchaserProfile.getOrderedPurchaseContactNumber();
            Address address = purchaserProfile.getOrderedPurchaseProfileAddress();

            OrderUserInfoResponse orderUserInfoResponse = new OrderUserInfoResponse(userId, contactNumber, address);

            // 주문한 상품 및 옵션 정보 가져오기
            Long totalPrice = 0L;
            String productOrderId = order.getId();
            DeliveryStatus deliveryStatus = order.getDeliveryStatus();
            LocalDate orderedTime = order.getOrderedTime();
            List<OrderProductListResponse> orderProductList = new ArrayList<>();

            List<OrderedProduct> orderedProducts = orderedProductRepository.findAllByProductOrder(order);
            for (OrderedProduct orderedProduct : orderedProducts) {
                List<OrderOptionListResponse> orderOptionList = new ArrayList<>();
                Long productOptionId = orderedProduct.getProductOptionId();
                int productOptionCount = orderedProduct.getProductOptionCount();

                Optional<ProductOption> maybeProductOption = productOptionRepository.findByIdWithProduct(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    log.info("ProductOption with product option ID '{}' not found", productOptionId);
                    return null;
                }

                // 상품 정보 확인을 위해 가져옴
                ProductOption productOption = maybeProductOption.get();
                Product product = productOption.getProduct();

                String optionName = productOption.getOptionName();
                Long optionPrice = productOption.getOptionPrice();
                Long totalOptionPrice = optionPrice * productOptionCount;
                Long productId = product.getId();
                String productName = product.getProductName();

                totalPrice = totalPrice + totalOptionPrice;

                OrderOptionListResponse orderOptionListResponse
                        = new OrderOptionListResponse(productOptionId, optionName, productOptionCount);
                orderOptionList.add(orderOptionListResponse);

                OrderProductListResponse orderProductListResponse
                        = new OrderProductListResponse(productId, productName, orderOptionList);
                orderProductList.add(orderProductListResponse);
            }

            OrderDetailInfoResponse orderDetailInfoResponse
                    = new OrderDetailInfoResponse(productOrderId, totalPrice, orderedTime, deliveryStatus);
            OrderListResponseFormForAdmin orderListResponseFormForAdmin
                    = new OrderListResponseFormForAdmin(orderUserInfoResponse, orderProductList, orderDetailInfoResponse);
            orderListResponseFormForAdmins.add(orderListResponseFormForAdmin);
        }
        return orderListResponseFormForAdmins;
    }

    // 사용자의 주문 내역 확인
    @Override
    public List<OrderListResponseFormForUser> getMyOrderListForUser(String userToken) {

        User user = authenticationService.findUserByUserToken(userToken);

        // 모든 주문 내역 가져오기
        List<ProductOrder> orderList = orderRepository.findAllByUserWithUser(user);

        List<OrderListResponseFormForUser> orderListResponseFormForUsers = new ArrayList<>();

        for (ProductOrder order : orderList) {

            // 주문한 상품 및 옵션 정보 가져오기
            Long totalPrice = 0L;
            String productOrderId = order.getId();
            DeliveryStatus deliveryStatus = order.getDeliveryStatus();
            LocalDate orderedTime = order.getOrderedTime();
            List<OrderProductListResponse> orderProductList = new ArrayList<>();

            List<OrderedProduct> orderedProducts = orderedProductRepository.findAllByProductOrder(order);
            for (OrderedProduct orderedProduct : orderedProducts) {
                List<OrderOptionListResponse> orderOptionList = new ArrayList<>();
                Long productOptionId = orderedProduct.getProductOptionId();
                int productOptionCount = orderedProduct.getProductOptionCount();

                Optional<ProductOption> maybeProductOption = productOptionRepository.findByIdWithProduct(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    log.info("ProductOption with product option ID '{}' not found", productOptionId);
                    return null;
                }

                // 상품 정보 확인을 위해 가져옴
                ProductOption productOption = maybeProductOption.get();
                Product product = productOption.getProduct();

                String optionName = productOption.getOptionName();
                Long optionPrice = productOption.getOptionPrice();
                Long totalOptionPrice = optionPrice * productOptionCount;
                Long productId = product.getId();
                String productName = product.getProductName();

                totalPrice = totalPrice + totalOptionPrice;

                OrderOptionListResponse orderOptionListResponse
                        = new OrderOptionListResponse(productOptionId, optionName, productOptionCount);
                orderOptionList.add(orderOptionListResponse);

                OrderProductListResponse orderProductListResponse
                        = new OrderProductListResponse(productId, productName, orderOptionList);
                orderProductList.add(orderProductListResponse);
            }

            OrderDetailInfoResponse orderDetailInfoResponse
                    = new OrderDetailInfoResponse(productOrderId, totalPrice, orderedTime, deliveryStatus);
            OrderListResponseFormForUser orderListResponseFormForUser
                    = new OrderListResponseFormForUser(orderProductList, orderDetailInfoResponse);
            orderListResponseFormForUsers.add(orderListResponseFormForUser);
        }
        return orderListResponseFormForUsers;
    }

    // 주문 진행
    public void saveOrderedData(OrderedPurchaserProfileRequest profileRequest,
                                int totalAmount, User user, List<OrderedProductOptionRequest> orderedProductOptionRequestList) {

        final String purchaserName = profileRequest.getOrderedPurchaserName();
        final String purchaserContactNumber = profileRequest.getOrderedPurchaserContactNumber();
        final String purchaserEmail = profileRequest.getOrderedPurchaserEmail();
        final String purchaserAddress = profileRequest.getOrderedPurchaserAddress();
        final String purchaserZipCode = profileRequest.getOrderedPurchaserZipCode();
        final String purchaserAddressDetail = profileRequest.getOrderedPurchaserAddressDetail();

        // 주문 저장
        ProductOrder order = ProductOrder.builder()
                .user(user)
                .totalAmount(totalAmount)
                .orderedTime(LocalDate.now())
                .deliveryStatus(PREPARING)
                .build();

        orderRepository.save(order);

        // 주문 상품 저장
        for (OrderedProductOptionRequest optionRequest : orderedProductOptionRequestList) {
            Optional<ProductOption> maybeProductOption = productOptionRepository.findById(optionRequest.getProductOptionId());
            if (maybeProductOption.isEmpty()) {
                log.info("Can not find ProductOption");
            } else if (maybeProductOption.isPresent()) {
                ProductOption productOption = maybeProductOption.get();
                productOption.setStock(productOption.getStock() - optionRequest.getProductOptionCount());
                productOptionRepository.save(productOption);
                OrderedProduct orderedProduct = OrderedProduct.builder()
                        .productOrder(order)
                        .productOptionId(optionRequest.getProductOptionId())
                        .productOptionCount(optionRequest.getProductOptionCount())
                        .build();

                orderedProductRepository.save(orderedProduct);
            }
        }
        // 구매자 정보 저장
        OrderedPurchaserProfile purchaserProfile = OrderedPurchaserProfile.builder()
                .productOrder(order)
                .orderedPurchaseName(purchaserName)
                .orderedPurchaseContactNumber(purchaserContactNumber)
                .orderedPurchaseEmail(purchaserEmail)
                .orderedPurchaseProfileAddress(new Address(purchaserAddress, purchaserZipCode, purchaserAddressDetail))
                .build();

        orderedPurchaserProfileRepository.save(purchaserProfile);
    }
}

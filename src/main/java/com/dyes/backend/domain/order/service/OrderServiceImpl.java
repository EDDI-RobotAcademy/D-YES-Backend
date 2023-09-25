package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.delivery.entity.Delivery;
import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import com.dyes.backend.domain.delivery.repository.DeliveryRepository;
import com.dyes.backend.domain.event.entity.EventOrder;
import com.dyes.backend.domain.event.entity.EventProduct;
import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import com.dyes.backend.domain.event.repository.EventOrderRepository;
import com.dyes.backend.domain.event.repository.EventProductRepository;
import com.dyes.backend.domain.event.repository.EventPurchaseCountRepository;
import com.dyes.backend.domain.order.controller.form.*;
import com.dyes.backend.domain.order.entity.*;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.order.repository.OrderedPurchaserProfileRepository;
import com.dyes.backend.domain.order.service.admin.response.*;
import com.dyes.backend.domain.order.service.admin.response.form.OrderDetailDataResponseForAdminForm;
import com.dyes.backend.domain.order.service.admin.response.form.OrderInfoResponseFormForDashBoardForAdmin;
import com.dyes.backend.domain.order.service.admin.response.form.OrderListResponseFormForAdmin;
import com.dyes.backend.domain.order.service.admin.response.form.OrderManagementInfoResponseForAdmin;
import com.dyes.backend.domain.order.service.user.request.*;
import com.dyes.backend.domain.order.service.user.response.*;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.user.response.form.OrderDetailDataResponseForUserForm;
import com.dyes.backend.domain.order.service.user.response.form.OrderListResponseFormForUser;
import com.dyes.backend.domain.payment.entity.Payment;
import com.dyes.backend.domain.payment.repository.PaymentRepository;
import com.dyes.backend.domain.payment.service.PaymentService;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentApprovalRequest;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRefundOrderAndTokenAndReasonRequest;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductMainImage;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.user.entity.Address;
import com.dyes.backend.domain.user.entity.AddressBook;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.AddressBookRepository;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.utility.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.dyes.backend.domain.delivery.entity.DeliveryStatus.PREPARING;
import static com.dyes.backend.domain.user.entity.AddressBookOption.DEFAULT_OPTION;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    final private OrderRepository orderRepository;
    final private ProductOptionRepository productOptionRepository;
    final private ContainProductOptionRepository containProductOptionRepository;
    final private UserProfileRepository userProfileRepository;
    final private AddressBookRepository addressBookRepository;
    final private ProductMainImageRepository productMainImageRepository;
    final private OrderedProductRepository orderedProductRepository;
    final private OrderedPurchaserProfileRepository orderedPurchaserProfileRepository;
    final private DeliveryRepository deliveryRepository;
    final private CartService cartService;
    final private PaymentService paymentService;
    final private AuthenticationService authenticationService;
    final private RedisService redisService;
    final private ProductRepository productRepository;
    final private PaymentRepository paymentRepository;
    final private ReviewRepository reviewRepository;
    final private EventProductRepository eventProductRepository;
    final private EventOrderRepository eventOrderRepository;
    final private EventPurchaseCountRepository eventPurchaseCountRepository;

    public String purchaseReadyWithKakao(OrderProductRequestForm requestForm) throws JsonProcessingException {
        log.info("purchaseKakao start");

        OrderProductRequest request = new OrderProductRequest(
                requestForm.getUserToken(),
                requestForm.getOrderedPurchaserProfileRequest(),
                requestForm.getOrderedProductOptionRequestList(),
                requestForm.getTotalAmount(),
                requestForm.getFrom()
                );
        String redirectUrl = paymentService.paymentTemporaryDataSaveAndReturnRedirectView(request);

        return redirectUrl;
    }
    public boolean approvalPurchaseWithKakao (KakaoPaymentApprovalRequestForm requestForm) throws JsonProcessingException {
        log.info("approvalPurchaseKakao start");
        KakaoPaymentApprovalRequest request = new KakaoPaymentApprovalRequest(requestForm.getUserToken(), requestForm.getPg_token());
        PaymentTemporarySaveRequest result = paymentService.paymentApprovalRequest(request);
        if (result != null) {
            orderProduct(result);

            final String userToken = request.getUserToken();
            User user = authenticationService.findUserByUserToken(userToken);

            redisService.deletePaymentTemporarySaveData(user.getId());
            return true;
        }
        log.info("approvalPurchaseKakao end");
        return false;
    }
    public boolean rejectPurchaseWithKakao (KakaoPaymentRejectRequestForm requestForm) {
        boolean result = paymentService.paymentRejectWithKakao(requestForm);
        return result;
    }
    public boolean refundPurchaseWithKakao (KakaoPaymentRefundOrderAndTokenAndReasonRequest orderAndTokenAndReasonRequest,
                                            List<KakaoPaymentRefundProductOptionRequest> requestList) {
        boolean result = paymentService.paymentRefundRequest(orderAndTokenAndReasonRequest, requestList);
        return result;
    }
    // 상품 주문
    public void orderProduct(PaymentTemporarySaveRequest saveRequest) {
        log.info("orderProductInCart start");
        try {
            OrderedPurchaserProfileRequest profileRequest = OrderedPurchaserProfileRequest.builder()
                    .orderedPurchaserName(saveRequest.getOrderedPurchaserProfileRequest().getOrderedPurchaserName())
                    .orderedPurchaserContactNumber(saveRequest.getOrderedPurchaserProfileRequest().getOrderedPurchaserContactNumber())
                    .orderedPurchaserEmail(saveRequest.getOrderedPurchaserProfileRequest().getOrderedPurchaserEmail())
                    .orderedPurchaserAddress(saveRequest.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddress())
                    .orderedPurchaserZipCode(saveRequest.getOrderedPurchaserProfileRequest().getOrderedPurchaserZipCode())
                    .orderedPurchaserAddressDetail(saveRequest.getOrderedPurchaserProfileRequest().getOrderedPurchaserAddressDetail())
                    .build();

            final String userToken = saveRequest.getUserToken();
            final int totalAmount = saveRequest.getTotalAmount();
            final String tid = saveRequest.getTid();
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                return;
            }
            List<OrderedProductOptionRequest> orderedProductOptionRequestList = saveRequest.getOrderedProductOptionRequestList();

            saveOrderedData(profileRequest, totalAmount, tid, user, orderedProductOptionRequestList);

            // 유저 토큰으로 장바구니 찾기
            Cart cart = cartService.cartCheckFromUserToken(userToken);

            // 주문한 상품이 장바구니에 있으면 장바구니에서 목록 제거
            if (saveRequest.getFrom().equals("CART")) {
                // 장바구니에 담긴 상품 리스트 불러오기
                List<ContainProductOption> productOptionList = containProductOptionRepository.findAllByCart(cart);

                for (ContainProductOption containProductOption : productOptionList) {
                    for (OrderedProductOptionRequest orderedProductOptionRequest : orderedProductOptionRequestList)
                        if (Objects.equals(containProductOption.getOptionId(), orderedProductOptionRequest.getProductOptionId())) {
                            containProductOptionRepository.delete(containProductOption);
                        }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while ordering products in cart", e);
        }
    }
    // 상품을 주문하기 전에 확인하기
    @Override
    public OrderConfirmResponseFormForUser orderConfirm(OrderConfirmRequestForm requestForm) {
        try {
            OrderConfirmRequest request = new OrderConfirmRequest(requestForm.getUserToken());
            List<OrderConfirmProductRequest> requestList = requestForm.getRequestList();

            final String userToken = request.getUserToken();
            // 유저 정보 찾기
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                return null;
            }
            UserProfile userProfile = userProfileRepository.findByUser(user).get();
            List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);
            String address = "";
            String zipcode = "";
            String addressDetail = "";
            for(AddressBook addressBook : addressBookList) {
                if(addressBook.getAddressBookOption().equals(DEFAULT_OPTION)){
                    address = addressBook.getAddress().getAddress();
                    zipcode = addressBook.getAddress().getZipCode();
                    addressDetail = addressBook.getAddress().getAddressDetail();
                }
            }

            // 반환될 유저 정보
            OrderConfirmUserResponse userResponse = new OrderConfirmUserResponse();

            try {
                userResponse = OrderConfirmUserResponse.builder()
                        .email(userProfile.getEmail())
                        .contactNumber(userProfile.getContactNumber())
                        .address(address)
                        .zipCode(zipcode)
                        .addressDetail(addressDetail)
                        .build();
            } catch (NullPointerException e) {
                log.error("please set your user profile");
            }

            List<OrderConfirmProductResponse> productResponseList = new ArrayList<>();
            for (OrderConfirmProductRequest productRequest : requestList) {
                ProductOption productOption = productOptionRepository.findByIdWithProduct(productRequest.getProductOptionId()).get();
                log.info("productOption: " + productOption.getOptionName());

                ProductMainImage mainImage = productMainImageRepository.findByProductId(productOption.getProduct().getId()).get();
                log.info("mainImage: " + mainImage.getMainImg());

                // 반환될 상품 정보
                OrderConfirmProductResponse productResponse = OrderConfirmProductResponse.builder()
                        .productName(productOption.getProduct().getProductName())
                        .optionId(productOption.getId())
                        .optionPrice(productOption.getOptionPrice())
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
            Long productOrderId = order.getId();
            Delivery delivery = order.getDelivery();
            DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
            LocalDate orderedTime = order.getOrderedTime();
            List<OrderProductListResponse> orderProductList = new ArrayList<>();

            List<OrderedProduct> orderedProducts = orderedProductRepository.findAllByProductOrder(order);
            for (OrderedProduct orderedProduct : orderedProducts) {
                List<OrderOptionListResponse> orderOptionList = new ArrayList<>();
                Long productOptionId = orderedProduct.getProductOptionId();
                int productOptionCount = orderedProduct.getProductOptionCount();
                Long productId = orderedProduct.getProductId();
                String productName = orderedProduct.getProductName();

                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    log.info("ProductOption with product option ID '{}' not found", productOptionId);
                    return null;
                }

                // 상품 정보 확인을 위해 가져옴
                ProductOption productOption = maybeProductOption.get();

                String optionName = productOption.getOptionName();
                Long optionPrice = productOption.getOptionPrice();
                Long totalOptionPrice = optionPrice * productOptionCount;

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

    // 관리자의 신규 주문 내역 확인
    @Override
    public OrderInfoResponseFormForDashBoardForAdmin getAllNewOrderListForAdmin() {
        log.info("Finding New Order start");

        // 최종적으로 반환할 ResponseForm에 들어갈 Response
        List<OrderInfoResponseForAdmin> orderInfoResponseForAdminList = new ArrayList<>();
        List<OrderManagementInfoResponseForAdmin> createdOrderCountList = new ArrayList<>();

        // 이전 7일간의 내역을 조회
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        List<ProductOrder> productOrderList
                = orderRepository.findAllByOrderedTimeAfterOrderByOrderedTimeDesc(sevenDaysAgo);
        if(productOrderList.size() == 0) {
            log.info("No Orders found.");
            return null;
        }
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        int registeredOrderCountToday = 0;
        int registeredOrderCount1DayAgo = 0;
        int registeredOrderCount2DaysAgo = 0;
        int registeredOrderCount3DaysAgo = 0;
        int registeredOrderCount4DaysAgo = 0;
        int registeredOrderCount5DaysAgo = 0;
        int registeredOrderCount6DaysAgo = 0;

        // 상품 목록 조회 진행
        for (ProductOrder productOrder : productOrderList) {
            List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrder(productOrder);
            String representativeProductName= orderedProductList.get(0).getProductName();
            if (productOrder.getOrderedTime().equals(today)) {
                registeredOrderCountToday = registeredOrderCountToday + 1;
            } else if (productOrder.getOrderedTime().equals(today.minusDays(1))) {
                registeredOrderCount1DayAgo = registeredOrderCount1DayAgo + 1;
            } else if (productOrder.getOrderedTime().equals(today.minusDays(2))) {
                registeredOrderCount2DaysAgo = registeredOrderCount2DaysAgo + 1;
            } else if (productOrder.getOrderedTime().equals(today.minusDays(3))) {
                registeredOrderCount3DaysAgo = registeredOrderCount3DaysAgo + 1;
            } else if (productOrder.getOrderedTime().equals(today.minusDays(4))) {
                registeredOrderCount4DaysAgo = registeredOrderCount4DaysAgo + 1;
            } else if (productOrder.getOrderedTime().equals(today.minusDays(5))) {
                registeredOrderCount5DaysAgo = registeredOrderCount5DaysAgo + 1;
            } else if (productOrder.getOrderedTime().equals(today.minusDays(6))) {
                registeredOrderCount6DaysAgo = registeredOrderCount6DaysAgo + 1;
            }

            OrderInfoResponseForAdmin orderInfoResponseForAdmin
                    = new OrderInfoResponseForAdmin(productOrder.getId(), representativeProductName, productOrder.getOrderStatus(), productOrder.getOrderedTime(), productOrder.getAmount().getTotalAmount());
            orderInfoResponseForAdminList.add(orderInfoResponseForAdmin);

        }
        dateList.add(today);
        dateList.add(today.minusDays(1));
        dateList.add(today.minusDays(2));
        dateList.add(today.minusDays(3));
        dateList.add(today.minusDays(4));
        dateList.add(today.minusDays(5));
        dateList.add(today.minusDays(6));

        orderCountList.add(registeredOrderCountToday);
        orderCountList.add(registeredOrderCount1DayAgo);
        orderCountList.add(registeredOrderCount2DaysAgo);
        orderCountList.add(registeredOrderCount3DaysAgo);
        orderCountList.add(registeredOrderCount4DaysAgo);
        orderCountList.add(registeredOrderCount5DaysAgo);
        orderCountList.add(registeredOrderCount6DaysAgo);

        for (int i = 0; i < 7; i++) {
            OrderManagementInfoResponseForAdmin orderManagementInfoResponseForAdmin
                    = new OrderManagementInfoResponseForAdmin(dateList.get(i), orderCountList.get(i));
            createdOrderCountList.add(orderManagementInfoResponseForAdmin);
        }

        OrderInfoResponseFormForDashBoardForAdmin orderManagementInfoResponseForAdmin
                = new OrderInfoResponseFormForDashBoardForAdmin(orderInfoResponseForAdminList, createdOrderCountList);

        log.info("Finding New Orders successful");
        return orderManagementInfoResponseForAdmin;
    }

    // 사용자의 주문 내역 확인
    @Override
    public List<OrderListResponseFormForUser> getMyOrderListForUser(String userToken) {

        User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        // 모든 주문 내역 가져오기
        List<ProductOrder> orderList = orderRepository.findAllByUserWithUserAndDelivery(user);

        // 모든 리뷰 내역 가져오기
        List<Review> reviewList = reviewRepository.findAllByUserWithProductAndOrder(user);

        List<OrderListResponseFormForUser> orderListResponseFormForUsers = new ArrayList<>();

        for (ProductOrder order : orderList) {

            // 주문한 상품 및 옵션 정보 가져오기
            Long totalPrice = 0L;
            Long productOrderId = order.getId();
            Delivery delivery = order.getDelivery();
            DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
            LocalDate orderedTime = order.getOrderedTime();
            List<OrderProductListResponseForUser> orderProductList = new ArrayList<>();

            List<OrderedProduct> orderedProducts = orderedProductRepository.findAllByProductOrder(order);
            for (OrderedProduct orderedProduct : orderedProducts) {
                List<OrderOptionListResponse> orderOptionList = new ArrayList<>();
                Long productOptionId = orderedProduct.getProductOptionId();
                int productOptionCount = orderedProduct.getProductOptionCount();
                Long productId = orderedProduct.getProductId();
                String productName = orderedProduct.getProductName();

                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    log.info("ProductOption with product option ID '{}' not found", productOptionId);
                    return null;
                }

                // 상품 정보 확인을 위해 가져옴
                ProductOption productOption = maybeProductOption.get();
                Product product = productOption.getProduct();

                // 리뷰 내역에서 product로
                Long reviewId = null;
                for (Review review : reviewList) {
                    if (review.getProductOrder().getId().equals(order.getId())){
                        if (review.getProduct().getId().equals(product.getId())){
                            reviewId = review.getId();
                        }
                    }
                }

                String optionName = productOption.getOptionName();
                Long optionPrice = productOption.getOptionPrice();
                Long totalOptionPrice = optionPrice * productOptionCount;

                totalPrice = totalPrice + totalOptionPrice;

                OrderOptionListResponse orderOptionListResponse
                        = new OrderOptionListResponse(productOptionId, optionName, productOptionCount);
                orderOptionList.add(orderOptionListResponse);

                OrderProductListResponseForUser orderProductListResponse
                        = new OrderProductListResponseForUser(productId, productName, orderOptionList, reviewId);
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
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = OverMaxStockException.class)
    public void saveOrderedData(OrderedPurchaserProfileRequest profileRequest,
                                int totalAmount, String tid, User user, List<OrderedProductOptionRequest> orderedProductOptionRequestList) {

        final String purchaserName = profileRequest.getOrderedPurchaserName();
        final String purchaserContactNumber = profileRequest.getOrderedPurchaserContactNumber();
        final String purchaserEmail = profileRequest.getOrderedPurchaserEmail();
        final String purchaserAddress = profileRequest.getOrderedPurchaserAddress();
        final String purchaserZipCode = profileRequest.getOrderedPurchaserZipCode();
        final String purchaserAddressDetail = profileRequest.getOrderedPurchaserAddressDetail();

        // 주문 저장
        Delivery delivery = Delivery.builder()
                .deliveryStatus(PREPARING)
                .build();
        deliveryRepository.save(delivery);

        ProductOrder order = ProductOrder.builder()
                .user(user)
                .tid(tid)
                .orderStatus(OrderStatus.SUCCESS_PAYMENT)
                .amount(new OrderAmount(totalAmount, 0))
                .orderedTime(LocalDate.now())
                .delivery(delivery)
                .build();

        orderRepository.save(order);

        Optional<Payment> maybePayment = paymentRepository.findByTid(tid);
        if(maybePayment.isEmpty()){
            log.info("Can not find Payment data");
            return;
        }
        Payment payment = maybePayment.get();
        payment.setProductOrder(order);
        paymentRepository.save(payment);

        // 주문 상품 저장
        for (OrderedProductOptionRequest optionRequest : orderedProductOptionRequestList) {
            Optional<ProductOption> maybeProductOption = productOptionRepository.findById(optionRequest.getProductOptionId());
            if (maybeProductOption.isEmpty()) {
                log.info("Can not find ProductOption");
                return;
            } else if (maybeProductOption.isPresent()) {
                ProductOption productOption = maybeProductOption.get();
                Optional<Product> maybeProduct = productRepository.findById(productOption.getProduct().getId());
                if (maybeProduct.isEmpty()) {
                    log.info("Can not find ProductOption");
                    return;
                }
                Product product = maybeProduct.get();

                if (productOption.getStock() - optionRequest.getProductOptionCount() < 0) {
                    log.info("over max stock");
                        throw new OverMaxStockException("over max stock");
                }

                productOption.setStock(productOption.getStock() - optionRequest.getProductOptionCount());
                productOptionRepository.save(productOption);

                OrderedProduct orderedProduct = OrderedProduct.builder()
                        .productOrder(order)
                        .productId(product.getId())
                        .productName(product.getProductName())
                        .productOptionId(optionRequest.getProductOptionId())
                        .productOptionCount(optionRequest.getProductOptionCount())
                        .orderedProductStatus(OrderedProductStatus.PURCHASED)
                        .build();

                orderedProductRepository.save(orderedProduct);

                Optional<EventProduct> maybeEventProduct = eventProductRepository.findByProductOptionWithPurchaseCount(productOption);
                if (maybeEventProduct.isPresent()) {
                    EventOrder eventOrder = EventOrder.builder()
                            .eventProduct(maybeEventProduct.get())
                            .productOrder(order)
                            .build();
                    eventOrderRepository.save(eventOrder);

                    EventPurchaseCount count = maybeEventProduct.get().getEventPurchaseCount();
                    Integer nowCount = count.getNowCount() + 1;
                    count.setNowCount(nowCount);
                    eventPurchaseCountRepository.save(count);
                }
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
    public OrderDetailDataResponseForAdminForm orderDetailDataCombineForAdmin(Long orderId) {
        try {
            log.info("orderDetailDataCombineForAdmin start");

            Optional<ProductOrder> maybeOrder = orderRepository.findByStringIdWithDelivery(orderId);
            if (maybeOrder.isEmpty()){
                log.info("no order data");
                return null;
            }
            ProductOrder order = maybeOrder.get();

            List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrder(order);

            Optional<Payment> maybePayment = paymentRepository.findByProductOrder(order);
            if (maybePayment.isEmpty()) {
                log.info("no payment data");
                return null;
            }
            Payment payment = maybePayment.get();

            Optional<OrderedPurchaserProfile> maybeOrderedPurchaseProfile = orderedPurchaserProfileRepository.findByProductOrder(order);
            if (maybeOrderedPurchaseProfile.isEmpty()) {
                log.info("no purchaseProfile data");
                return null;
            }
            OrderedPurchaserProfile profile = maybeOrderedPurchaseProfile.get();

            OrderCombineOrderData orderData = OrderCombineOrderData.builder()
                    .productOrderId(order.getId())
                    .deliveryStatus(order.getDelivery().getDeliveryStatus())
                    .orderStatus(order.getOrderStatus())
                    .build();

            List<OrderCombineOrderedProductData> productDataList = new ArrayList<>();

            for (OrderedProduct orderedProduct : orderedProductList){
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(orderedProduct.getProductOptionId());
                if (maybeOrder.isPresent()){
                    ProductOption productOption = maybeProductOption.get();
                    OrderCombineOrderedProductData productData = OrderCombineOrderedProductData.builder()
                            .productId(orderedProduct.getProductId())
                            .productOptionId(orderedProduct.getProductOptionId())
                            .productOptionName(productOption.getOptionName())
                            .productOptionCount(orderedProduct.getProductOptionCount())
                            .productOptionPrice(productOption.getOptionPrice())
                            .productName(orderedProduct.getProductName())
                            .build();
                    productDataList.add(productData);
                }
            }

            OrderCombinePaymentData paymentData = OrderCombinePaymentData.builder()
                    .totalPrice(payment.getAmount().getTotal())
                    .deliveryFee(0)
                    .paymentPrice(0)
                    .paymentMethod(payment.getPayment_method_type())
                    .paymentDate(payment.getApproved_at())
                    .build();

            OrderCombineOrderedPurchaserProfileData profileData = OrderCombineOrderedPurchaserProfileData.builder()
                    .orderedPurchaseName(profile.getOrderedPurchaseName())
                    .orderedPurchaseContactNumber(profile.getOrderedPurchaseContactNumber())
                    .orderedPurchaseEmail(profile.getOrderedPurchaseEmail())
                    .address(profile.getOrderedPurchaseProfileAddress().getAddress())
                    .zipCode(profile.getOrderedPurchaseProfileAddress().getZipCode())
                    .addressDetail(profile.getOrderedPurchaseProfileAddress().getAddressDetail())
                    .build();

            OrderDetailDataResponseForAdminForm responseForm = OrderDetailDataResponseForAdminForm.builder()
                    .orderData(orderData)
                    .paymentData(paymentData)
                    .productDataList(productDataList)
                    .profileData(profileData)
                    .build();

            log.info("orderDetailDataCombineForAdmin end");

            return responseForm;
        } catch (Exception e) {
            log.error("Error occurred while confirm products in cart", e);
            return null;
        }
    }
    public OrderDetailDataResponseForUserForm orderDetailDataCombineForUser(Long orderId) {
        try {
            log.info("orderDetailDataCombineForUser start");

            Optional<ProductOrder> maybeOrder = orderRepository.findByStringIdWithDelivery(orderId);
            if (maybeOrder.isEmpty()){
                log.info("no order data");
                return null;
            }
            ProductOrder order = maybeOrder.get();

            List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrder(order);

            Optional<Payment> maybePayment = paymentRepository.findByProductOrder(order);
            if (maybePayment.isEmpty()) {
                log.info("no payment data");
                return null;
            }
            Payment payment = maybePayment.get();

            Optional<OrderedPurchaserProfile> maybeOrderedPurchaseProfile = orderedPurchaserProfileRepository.findByProductOrder(order);
            if (maybeOrderedPurchaseProfile.isEmpty()) {
                log.info("no purchaseProfile data");
                return null;
            }
            OrderedPurchaserProfile profile = maybeOrderedPurchaseProfile.get();

            OrderCombineOrderDataForUser orderData = OrderCombineOrderDataForUser.builder()
                    .productOrderId(order.getId())
                    .deliveryStatus(order.getDelivery().getDeliveryStatus())
                    .orderStatus(order.getOrderStatus())
                    .build();

            List<OrderCombineOrderedProductDataForUser> productDataList = new ArrayList<>();

            for (OrderedProduct orderedProduct : orderedProductList){
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(orderedProduct.getProductOptionId());
                if (maybeOrder.isPresent()){
                    ProductOption productOption = maybeProductOption.get();
                    OrderCombineOrderedProductDataForUser productData = OrderCombineOrderedProductDataForUser.builder()
                            .productOptionName(productOption.getOptionName())
                            .productOptionCount(orderedProduct.getProductOptionCount())
                            .productOptionPrice(productOption.getOptionPrice())
                            .productName(orderedProduct.getProductName())
                            .build();
                    productDataList.add(productData);
                }
            }

            OrderCombinePaymentDataForUser paymentData = OrderCombinePaymentDataForUser.builder()
                    .totalPrice(payment.getAmount().getTotal())
                    .deliveryFee(0)
                    .paymentPrice(0)
                    .paymentMethod(payment.getPayment_method_type())
                    .paymentDate(payment.getApproved_at())
                    .build();

            OrderCombineOrderedPurchaserProfileDataForUser profileData = OrderCombineOrderedPurchaserProfileDataForUser.builder()
                    .orderedPurchaseName(profile.getOrderedPurchaseName())
                    .orderedPurchaseContactNumber(profile.getOrderedPurchaseContactNumber())
                    .orderedPurchaseEmail(profile.getOrderedPurchaseEmail())
                    .address(profile.getOrderedPurchaseProfileAddress().getAddress())
                    .zipCode(profile.getOrderedPurchaseProfileAddress().getZipCode())
                    .addressDetail(profile.getOrderedPurchaseProfileAddress().getAddressDetail())
                    .build();

            OrderDetailDataResponseForUserForm responseForm = OrderDetailDataResponseForUserForm.builder()
                    .orderData(orderData)
                    .paymentData(paymentData)
                    .productDataList(productDataList)
                    .profileData(profileData)
                    .build();

            log.info("orderDetailDataCombineForAdmin end");

            return responseForm;
        } catch (Exception e) {
            log.error("Error occurred while confirm products in cart", e);
            return null;
        }
    }
    public class OverMaxStockException extends RuntimeException {
        public OverMaxStockException(String message) {
            super(message);
        }
    }

}

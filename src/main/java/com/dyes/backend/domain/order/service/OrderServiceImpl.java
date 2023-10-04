package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
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
import com.dyes.backend.domain.order.service.admin.response.form.*;
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
import jakarta.persistence.criteria.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.dyes.backend.domain.delivery.entity.DeliveryStatus.PREPARING;
import static com.dyes.backend.domain.order.entity.OrderStatus.*;
import static com.dyes.backend.domain.order.entity.OrderedProductStatus.*;
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
    final private AdminService adminService;

    // 카카오로 상품 구매
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

    public boolean approvalPurchaseWithKakao(KakaoPaymentApprovalRequestForm requestForm) throws JsonProcessingException {
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

    public boolean rejectPurchaseWithKakao(KakaoPaymentRejectRequestForm requestForm) {
        boolean result = paymentService.paymentRejectWithKakao(requestForm);
        return result;
    }

    public boolean refundPurchaseWithKakao(KakaoPaymentRefundOrderAndTokenAndReasonRequest orderAndTokenAndReasonRequest,
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

    // 결제 전 주문 요청내역 확인
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
            for (AddressBook addressBook : addressBookList) {
                if (addressBook.getAddressBookOption().equals(DEFAULT_OPTION)) {
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
            OrderStatus orderStatus = order.getOrderStatus();
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
                        = new OrderOptionListResponse(productOptionId, optionName, productOptionCount, orderedProduct.getOrderedProductStatus());
                orderOptionList.add(orderOptionListResponse);

                OrderProductListResponse orderProductListResponse
                        = new OrderProductListResponse(productId, productName, orderOptionList);
                orderProductList.add(orderProductListResponse);
            }

            OrderDetailInfoResponse orderDetailInfoResponse
                    = new OrderDetailInfoResponse(productOrderId, totalPrice, orderedTime, deliveryStatus, orderStatus);
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
        if (productOrderList.size() == 0) {
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
            String representativeProductName = orderedProductList.get(0).getProductName();
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
            OrderStatus orderStatus = order.getOrderStatus();
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
                    if (review.getProductOrder().getId().equals(order.getId())) {
                        if (review.getProduct().getId().equals(product.getId())) {
                            reviewId = review.getId();
                        }
                    }
                }

                String optionName = productOption.getOptionName();
                Long optionPrice = productOption.getOptionPrice();
                Long totalOptionPrice = optionPrice * productOptionCount;

                totalPrice = totalPrice + totalOptionPrice;

                OrderOptionListResponse orderOptionListResponse
                        = new OrderOptionListResponse(productOptionId, optionName, productOptionCount, orderedProduct.getOrderedProductStatus());
                orderOptionList.add(orderOptionListResponse);

                OrderProductListResponseForUser orderProductListResponse
                        = new OrderProductListResponseForUser(productId, productName, orderOptionList, reviewId);
                orderProductList.add(orderProductListResponse);
            }

            OrderDetailInfoResponse orderDetailInfoResponse
                    = new OrderDetailInfoResponse(productOrderId, totalPrice, orderedTime, deliveryStatus, orderStatus);
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
                .orderStatus(SUCCESS_PAYMENT)
                .amount(new OrderAmount(totalAmount, 0))
                .orderedTime(LocalDate.now())
                .delivery(delivery)
                .build();

        orderRepository.save(order);

        Optional<Payment> maybePayment = paymentRepository.findByTid(tid);
        if (maybePayment.isEmpty()) {
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

    // 관리자의 주문 내역 상세 읽기
    @Override
    public OrderDetailDataResponseForAdminForm orderDetailDataCombineForAdmin(Long orderId) {
        try {
            log.info("orderDetailDataCombineForAdmin start");

            Optional<ProductOrder> maybeOrder = orderRepository.findByStringIdWithDelivery(orderId);
            if (maybeOrder.isEmpty()) {
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

            for (OrderedProduct orderedProduct : orderedProductList) {
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(orderedProduct.getProductOptionId());
                if (maybeOrder.isPresent()) {
                    ProductOption productOption = maybeProductOption.get();
                    OrderCombineOrderedProductData productData = OrderCombineOrderedProductData.builder()
                            .productId(orderedProduct.getProductId())
                            .productOptionId(orderedProduct.getProductOptionId())
                            .productOptionName(productOption.getOptionName())
                            .productOptionCount(orderedProduct.getProductOptionCount())
                            .productOptionPrice(productOption.getOptionPrice())
                            .productName(orderedProduct.getProductName())
                            .orderedProductStatus(orderedProduct.getOrderedProductStatus())
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

    // 사용자의 주문 내역 상세 읽기
    @Override
    public OrderDetailDataResponseForUserForm orderDetailDataCombineForUser(Long orderId) {
        try {
            log.info("orderDetailDataCombineForUser start");

            Optional<ProductOrder> maybeOrder = orderRepository.findByStringIdWithDelivery(orderId);
            if (maybeOrder.isEmpty()) {
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

            for (OrderedProduct orderedProduct : orderedProductList) {
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(orderedProduct.getProductOptionId());
                if (maybeOrder.isPresent()) {
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

    // 배송완료 주문은 환불 신청시 환불 대기 상태로 변경
    @Override
    public boolean orderedProductWaitingRefund(OrderedProductChangeStatusRequestForm requestForm) {
        final String userToken = requestForm.getUserToken();
        final Long orderId = requestForm.getOrderId();
        final List<Long> productOptionIdList = requestForm.getProductOptionId();
        final String refundReason = requestForm.getRefundReason();

        try {
            final User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                log.info("There are no matching users");
                return false;
            }

            Optional<ProductOrder> maybeOrder = orderRepository.findById(orderId);
            if (maybeOrder.isEmpty()) {
                log.info("There are no matching orders");
                return false;
            }
            ProductOrder order = maybeOrder.get();

            List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrder(order);
            for (OrderedProduct orderedProduct : orderedProductList) {
                for (Long productOptionId : productOptionIdList) {
                    if (orderedProduct.getProductOptionId().equals(productOptionId)) {
                        orderedProduct.setOrderedProductStatus(WAITING_REFUND);
                        orderedProduct.setRefundReason(refundReason);
                        orderedProductRepository.save(orderedProduct);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while change ordered products status", e);
            return false;
        }

    }

    // 관리자의 월 주문 통계 데이터 확인
    @Override
    public MonthlyOrdersStatisticsResponseForm getMonthlyOrders() {
        final LocalDate currentDate = LocalDate.now();
        final LocalDate firstDayOfCurrentMonth = currentDate.withDayOfMonth(1);
        final LocalDate lastDayOfCurrentMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        log.info("This month Period: " + firstDayOfCurrentMonth + " ~ " + lastDayOfCurrentMonth);

        final LocalDate lastDayOfPreviousMonth = firstDayOfCurrentMonth.minusDays(1);
        final LocalDate firstDayOfPreviousMonth = lastDayOfPreviousMonth.withDayOfMonth(1);
        log.info("This previous Period: " + firstDayOfPreviousMonth + " ~ " + lastDayOfPreviousMonth);

        int totalOrdersCount = 0;
        int completedOrders = 0;
        int cancelledOrders = 0;
        int totalOrdersAmount = 0;
        int totalPreviousOrdersAmount = 0;
        Long monthOverMonthGrowthRate = 0L;
        List<Integer> orderCountListByDay = new ArrayList<>();

        // 당월 주문 내역 가져오기
        List<ProductOrder> productOrderList
                = orderRepository.findByOrderedTimeBetween(firstDayOfCurrentMonth, lastDayOfCurrentMonth);

        // 주문 건수 가져오기(합계, 완료, 취소)
        for (ProductOrder productOrder : productOrderList) {
            totalOrdersCount = totalOrdersCount + 1;
            if (productOrder.getOrderStatus().equals(SUCCESS_PAYMENT)) {
                completedOrders = completedOrders + 1;
            } else if (productOrder.getOrderStatus().equals(CANCEL_PAYMENT)) {
                cancelledOrders = cancelledOrders + 1;
            }

            totalOrdersAmount = totalOrdersAmount
                    + (productOrder.getAmount().getTotalAmount() - productOrder.getAmount().getRefundedAmount());
        }

        // 일별 주문 수량 가져오기
        LocalDate date = firstDayOfCurrentMonth;
        while (!date.isAfter(lastDayOfCurrentMonth)) {
            int tmpOrdersCount = 0;
            for (ProductOrder productOrder : productOrderList) {
                if (productOrder.getOrderedTime().equals(date)) {
                    tmpOrdersCount = tmpOrdersCount + 1;
                }
            }
            orderCountListByDay.add(tmpOrdersCount);
            date = date.plusDays(1);
        }

        // 전월 대비 변동율 구하기
        List<ProductOrder> previousProductOrderList
                = orderRepository.findByOrderedTimeBetween(firstDayOfPreviousMonth, lastDayOfPreviousMonth);
        for (ProductOrder previousProductOrder : previousProductOrderList) {
            totalPreviousOrdersAmount = totalPreviousOrdersAmount
                    + (previousProductOrder.getAmount().getTotalAmount() - previousProductOrder.getAmount().getRefundedAmount());
        }

        if (totalPreviousOrdersAmount != 0) {
            monthOverMonthGrowthRate
                    = Math.round(((double) (totalOrdersAmount - totalPreviousOrdersAmount) / totalPreviousOrdersAmount) * 100);
        } else {
            log.info("Previous data does not exist, previous total amount: " + totalPreviousOrdersAmount);
            monthOverMonthGrowthRate = 0L;
        }

        MonthlyOrdersStatisticsResponseForm monthlyOrdersStatisticsResponseForm
                = new MonthlyOrdersStatisticsResponseForm(
                totalOrdersCount,
                completedOrders,
                cancelledOrders,
                totalOrdersAmount,
                monthOverMonthGrowthRate,
                orderCountListByDay);
        return monthlyOrdersStatisticsResponseForm;
    }

    // 관리자의 환불 목록 확인
    @Override
    public List<OrderRefundListResponseFormForAdmin> getAllOrderRefundListForAdmin() {
        log.info("Finding Refund Order List start");

        try {

            List<OrderRefundListResponseFormForAdmin> orderRefundListResponseFormForAdminList = new ArrayList<>();

            // 주문 목록 가져오기
            List<ProductOrder> orderList = orderRepository.findAllWithUser();
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

                // 주문 정보 가져오기
                int totalPrice = order.getAmount().getTotalAmount();
                int refundPrice = order.getAmount().getRefundedAmount();

                Long productOrderId = order.getId();
                Delivery delivery = order.getDelivery();
                DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
                LocalDate orderedTime = order.getOrderedTime();
                String refundReason = "";
                OrderedProductStatus orderedProductStatus = null;

                // 주문 상품 옵션 가져오기
                List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrderAndStatus(order);
                List<OrderedProductStatus> orderedProductStatusList = new ArrayList<>();
                for (OrderedProduct orderedProduct : orderedProductList) {
                    refundReason = orderedProduct.getRefundReason();
                    orderedProductStatusList.add(orderedProduct.getOrderedProductStatus());
                }

                if (orderedProductStatusList.contains(WAITING_REFUND)) {
                    orderedProductStatus = WAITING_REFUND;
                } else if (orderedProductStatusList.contains(REFUNDED) && !orderedProductStatusList.contains(WAITING_REFUND)) {
                    orderedProductStatus = REFUNDED;
                } else if ((orderedProductStatusList.contains(PAYBACK))) {
                    orderedProductStatus = PAYBACK;
                }

                if (orderedProductStatus != null) {
                    if (orderedProductStatus.equals(WAITING_REFUND) || orderedProductStatus.equals(REFUNDED) || orderedProductStatus.equals(PAYBACK)) {
                        OrderRefundDetailInfoResponse orderRefundDetailInfoResponse
                                = new OrderRefundDetailInfoResponse(
                                productOrderId,
                                totalPrice,
                                refundPrice,
                                orderedTime,
                                deliveryStatus,
                                orderedProductStatus,
                                refundReason);

                        OrderRefundListResponseFormForAdmin orderRefundListResponseFormForAdmin
                                = new OrderRefundListResponseFormForAdmin(orderUserInfoResponse, orderRefundDetailInfoResponse);
                        orderRefundListResponseFormForAdminList.add(orderRefundListResponseFormForAdmin);
                    }
                }
            }
            log.info("Finding Refund Order List end");
            return orderRefundListResponseFormForAdminList;
        } catch (Exception e) {
            log.error("Error occurred while find refunded order list", e);
            return null;
        }
    }

    // 관리자의 환불 주문건의 간략한 정보 확인
    @Override
    public OrderProductListResponse getRefundSummaryInfo(Long productOrderId) {
        try {
            log.info("getRefundSummaryInfo start");

            Optional<ProductOrder> maybeOrder = orderRepository.findByStringIdWithDelivery(productOrderId);
            if (maybeOrder.isEmpty()) {
                log.info("no order data");
                return null;
            }
            ProductOrder order = maybeOrder.get();

            List<OrderedProduct> orderedProductList = orderedProductRepository.findAllByProductOrder(order);

            List<OrderOptionListResponse> orderOptionListResponseList = new ArrayList<>();
            Long productId = 0L;
            String productName = "";
            for (OrderedProduct orderedProduct : orderedProductList) {
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(orderedProduct.getProductOptionId());
                if (maybeOrder.isPresent()) {
                    ProductOption productOption = maybeProductOption.get();
                    OrderOptionListResponse orderOptionListResponse
                            = OrderOptionListResponse.builder()
                            .optionId(productOption.getId())
                            .optionName(productOption.getOptionName())
                            .optionCount(orderedProduct.getProductOptionCount())
                            .orderProductStatus(orderedProduct.getOrderedProductStatus())
                            .refundReason(orderedProduct.getRefundReason())
                            .build();
                    orderOptionListResponseList.add(orderOptionListResponse);
                }
                productId = orderedProduct.getProductId();
                productName = orderedProduct.getProductName();
            }
            OrderProductListResponse orderProductListResponse = new OrderProductListResponse(productId, productName, orderOptionListResponseList);

            log.info("getRefundSummaryInfo end");
            return orderProductListResponse;
        } catch (Exception e) {
            log.error("Error occurred while get order summary Info", e);
            return null;
        }
    }

    public class OverMaxStockException extends RuntimeException {
        public OverMaxStockException(String message) {
            super(message);
        }
    }

}

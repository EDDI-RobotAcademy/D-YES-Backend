package com.dyes.backend.domain.payment.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.event.entity.EventOrder;
import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRefundRequestForm;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRejectRequestForm;
import com.dyes.backend.domain.order.entity.OrderAmount;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.OrderedProductStatus;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.order.service.user.request.KakaoPaymentRefundProductOptionRequest;
import com.dyes.backend.domain.order.service.user.request.KakaoPaymentRefundRequest;
import com.dyes.backend.domain.order.service.user.request.OrderProductRequest;
import com.dyes.backend.domain.order.service.user.request.OrderedProductOptionRequest;
import com.dyes.backend.domain.order.service.user.response.KakaoPaymentRefundResponse;
import com.dyes.backend.domain.payment.entity.Payment;
import com.dyes.backend.domain.payment.entity.PaymentAmount;
import com.dyes.backend.domain.payment.entity.PaymentCardInfo;
import com.dyes.backend.domain.payment.entity.RefundedPayment;
import com.dyes.backend.domain.payment.repository.PaymentRepository;
import com.dyes.backend.domain.payment.repository.RefundedPaymentRepository;
import com.dyes.backend.domain.payment.service.request.*;
import com.dyes.backend.domain.payment.service.response.KakaoApproveResponse;
import com.dyes.backend.domain.payment.service.response.KakaoPaymentReadyResponse;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.utility.provider.KakaoPaymentSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dyes.backend.domain.order.entity.OrderStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    final private RedisService redisService;
    final private PaymentRepository paymentRepository;
    final private KakaoPaymentSecretsProvider kakaoPaymentSecretsProvider;
    final private RestTemplate restTemplate;
    final private ProductOptionRepository productOptionRepository;
    final private AuthenticationService authenticationService;
    final private OrderRepository orderRepository;
    final private RefundedPaymentRepository refundedPaymentRepository;
    final private OrderedProductRepository orderedProductRepository;
    final private AdminRepository adminRepository;
    public KakaoPaymentReadyResponse paymentRequest(KakaoPaymentRequest request) {
        log.info("paymentRequest start");
        try{
            final String requestUrl = kakaoPaymentSecretsProvider.getKakaoPaymentRequestUrl();

            final String cid = kakaoPaymentSecretsProvider.getCid();
            final String partnerOrderId = request.getPartner_order_id();
            final String partnerUserId = request.getPartner_user_id();
            final String itemName = request.getItem_name();
            final String quantity = String.valueOf(request.getQuantity());
            final String totalAmount = String.valueOf(request.getTotal_amount());
            final String vatAmount = String.valueOf(request.getTotal_amount()/11);
            final String approvalUrl = kakaoPaymentSecretsProvider.getKakaoPaymentApprovalUrl();
            final String cancelUrl = kakaoPaymentSecretsProvider.getKakaoPaymentCancelUrl();
            final String failUrl = kakaoPaymentSecretsProvider.getKakaoPaymentFailUrl();

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("cid", cid);
            parameters.add("partner_order_id", partnerOrderId);
            parameters.add("partner_user_id", partnerUserId);
            parameters.add("item_name", itemName);
            parameters.add("quantity", quantity);
            parameters.add("total_amount", totalAmount);
            parameters.add("vat_amount", vatAmount);
            parameters.add("tax_free_amount", "0");
            parameters.add("approval_url", approvalUrl);
            parameters.add("cancel_url", cancelUrl);
            parameters.add("fail_url", failUrl);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

            KakaoPaymentReadyResponse response = restTemplate.postForObject(requestUrl, requestEntity, KakaoPaymentReadyResponse.class);

            log.info("paymentRequest end");
            return response;
        } catch (HttpServerErrorException e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return null;
        }
    }
    public PaymentTemporarySaveRequest paymentApprovalRequest(KakaoPaymentApprovalRequest request) throws JsonProcessingException {
        log.info("paymentApprovalRequest start");

        final String userToken = request.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        final String userId = user.getId();

        PaymentTemporarySaveRequest saveRequest = redisService.getPaymentTemporarySaveData(userId);

        if (saveRequest == null) {
            return null;
        }

        final String pgToken = request.getPgToken();
        final String tid = saveRequest.getTid();
        final String cid = kakaoPaymentSecretsProvider.getCid();
        final String approveUrl = kakaoPaymentSecretsProvider.getKakaoPaymentApproveUrl();
        final String partnerOrderId = saveRequest.getPartnerOrderId();
        final String partnerUserId = saveRequest.getPartnerUserId();

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", tid);
        parameters.add("partner_order_id", partnerOrderId);
        parameters.add("partner_user_id", partnerUserId);
        parameters.add("pg_token", pgToken);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        try {
            KakaoApproveResponse approveResponse = restTemplate.postForObject(approveUrl, requestEntity, KakaoApproveResponse.class);
            if (approveResponse != null) {
                if(paymentCompleteAndSaveWithKakao(approveResponse)) {
                    log.info("paymentApprovalRequest end");
                    return saveRequest;
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
        }
        return null;
    }
    public boolean paymentRejectWithKakao(KakaoPaymentRejectRequestForm requestForm) {
        KakaoPaymentRejectRequest request = new KakaoPaymentRejectRequest(requestForm.getUserToken());

        final String userToken = request.getUserToken();
        try {
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                return false;
            }
            redisService.deletePaymentTemporarySaveData(user.getId());
            return true;
        } catch (Exception e) {
            log.error("Failed to reject: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean paymentRefundRequest(KakaoPaymentRefundOrderAndTokenAndReasonRequest orderAndTokenAndReasonRequest,
                                        List<KakaoPaymentRefundProductOptionRequest> requestList) {
        log.info("paymentRefundRequest start");

        final String userToken = orderAndTokenAndReasonRequest.getUserToken();
        final Long orderId = orderAndTokenAndReasonRequest.getOrderId();
        final String refundReason = orderAndTokenAndReasonRequest.getRefundReason();

        try {
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                log.info("There are no matching users");
                return false;
            }

            Optional<Admin> maybeAdmin = adminRepository.findByUser(user);

            Optional<ProductOrder> maybeOrder = orderRepository.findByIdWithUser(orderId);
            if (maybeOrder.isEmpty()) {
                log.info("There are no matching orders");
                return false;
            }
            ProductOrder order = maybeOrder.get();

            User userInOrder = order.getUser();

            if (!user.getId().equals(userInOrder.getId()) && maybeAdmin.isEmpty()) {
                log.info("There are no matching user and user in order");
                return false;
            }

            final String cid = kakaoPaymentSecretsProvider.getCid();
            final String tid = order.getTid();
            final String refundUrl = kakaoPaymentSecretsProvider.getKakaoPaymentRefundUrl();
            Integer cancelAmount = 0;

            List<ProductOption> refundProductOptionList = new ArrayList<>();

            for (KakaoPaymentRefundProductOptionRequest optionRequest : requestList) {
                Long productOptionId = optionRequest.getProductOptionId();
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    log.info("There are no matching options");
                    return false;
                }
                ProductOption productOption = maybeProductOption.get();

                cancelAmount += productOption.getOptionPrice().intValue();

                refundProductOptionList.add(productOption);
            }

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("cid", cid);
            parameters.add("tid", tid);
            parameters.add("cancel_amount", String.valueOf(cancelAmount));
            parameters.add("cancel_tax_free_amount", "0");
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

            try {
                KakaoPaymentRefundResponse response = restTemplate.postForObject(refundUrl, requestEntity, KakaoPaymentRefundResponse.class);
                log.info("response: " + response);
                if (response.getApproved_cancel_amount() != null) {

                    OrderAmount orderAmount = order.getAmount();
                    Integer existingAmount = orderAmount.getTotalAmount();
                    Integer existingRefundAmount = orderAmount.getRefundedAmount();
                    orderAmount.setTotalAmount(existingAmount - cancelAmount);
                    orderAmount.setRefundedAmount(existingRefundAmount + cancelAmount);
                    order.setAmount(orderAmount);

                    RefundedPayment refundedPayment = RefundedPayment.builder()
                            .aid(response.getAid())
                            .tid(response.getTid())
                            .user(user)
                            .refundReason(refundReason)
                            .approved_cancel_amount(response.getApproved_cancel_amount().getTotal())
                            .canceled_at(response.getCanceled_at())
                            .build();

                    List<OrderedProduct> productList = orderedProductRepository.findAllByProductOrder(order);
                    Set<Long> refundRequestOptionIdStream = requestList.stream()
                            .map(KakaoPaymentRefundProductOptionRequest :: getProductOptionId)
                            .collect(Collectors.toSet());

                    for (OrderedProduct orderedProduct : productList) {
                        if(refundRequestOptionIdStream.contains(orderedProduct.getProductOptionId()) && orderedProduct.getOrderedProductStatus() != OrderedProductStatus.REFUNDED){

                            orderedProduct.setOrderedProductStatus(OrderedProductStatus.REFUNDED);
                            orderedProduct.setRefundReason(refundReason);
                            orderedProductRepository.save(orderedProduct);
                            }
                    }
                    boolean isAllRefunded = productList.stream().allMatch(orderedProduct -> orderedProduct.getOrderedProductStatus() == OrderedProductStatus.REFUNDED);

                    if (response.getStatus().equals(CANCEL_PAYMENT)) {
                        order.setOrderStatus(CANCEL_PAYMENT);
                    } else if (order.getAmount().getTotalAmount() == 0) {
                        order.setOrderStatus(CANCEL_PAYMENT);
                    } else if (isAllRefunded) {
                        order.setOrderStatus(CANCEL_PAYMENT);
                    }
                    else {
                        order.setOrderStatus(PART_CANCEL_PAYMENT);
                    }

                    orderRepository.save(order);
                    refundedPaymentRepository.save(refundedPayment);
                    log.info("paymentRefundRequest end");
                    return true;
                }
            } catch (HttpClientErrorException | HttpServerErrorException | NullPointerException e) {
                log.error("Failed connect to server: {}", e.getMessage(), e);
                return false;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to reject: {}", e.getMessage(), e);
            return false;
        }
    }
    public void paymentEventProductRefundRequest(EventOrder eventOrder) {
        log.info("paymentRefundRequest start");

        try {
            Optional<ProductOrder> maybeOrder = orderRepository.findById(eventOrder.getProductOrder().getId());
            if (maybeOrder.isEmpty()) {
                log.info("maybeOrder isEmpty");
                return;
            }
            ProductOrder order = maybeOrder.get();
            User user = order.getUser();
            EventPurchaseCount count = eventOrder.getEventProduct().getEventPurchaseCount();

            final String cid = kakaoPaymentSecretsProvider.getCid();
            final String tid = order.getTid();
            final String refundUrl = kakaoPaymentSecretsProvider.getKakaoPaymentRefundUrl();

            Optional<Payment> maybePayment = paymentRepository.findByProductOrder(order);
            if (maybePayment.isEmpty()){
                return;
            }
            Payment payment = maybePayment.get();
            PaymentAmount paymentAmount = payment.getAmount();

            Long cancelAmount;
            if ((count.getNowCount() / count.getTargetCount()) < 1) {
                cancelAmount = (long) (paymentAmount.getTotal() * 3/10 * ((count.getNowCount() / count.getTargetCount())));
            } else {
                cancelAmount = (long) (paymentAmount.getTotal() * 3/10);
            }

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("cid", cid);
            parameters.add("tid", tid);
            parameters.add("cancel_amount", String.valueOf(cancelAmount));
            parameters.add("cancel_tax_free_amount", "0");
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

            try {
                KakaoPaymentRefundResponse response = restTemplate.postForObject(refundUrl, requestEntity, KakaoPaymentRefundResponse.class);
                if (response.getApproved_cancel_amount() != null) {

                    OrderAmount orderAmount = order.getAmount();
                    orderAmount.setRefundedAmount(Math.toIntExact(cancelAmount));
                    order.setAmount(orderAmount);

                    if (response.getStatus().equals(CANCEL_PAYMENT)) {
                        order.setOrderStatus(EVENT_PAYBACK);
                    } else {
                        order.setOrderStatus(EVENT_PAYBACK);
                    }

                    RefundedPayment refundedPayment = RefundedPayment.builder()
                            .aid(response.getAid())
                            .tid(response.getTid())
                            .user(user)
                            .approved_cancel_amount(response.getApproved_cancel_amount().getTotal())
                            .canceled_at(response.getCanceled_at())
                            .build();

                    List<OrderedProduct> productList = orderedProductRepository.findAllByProductOrder(order);
                        for (OrderedProduct orderedProduct : productList) {
                            orderedProduct.setOrderedProductStatus(OrderedProductStatus.PAYBACK);
                            orderedProductRepository.save(orderedProduct);
                        }

                    orderRepository.save(order);
                    refundedPaymentRepository.save(refundedPayment);
                    log.info("paymentRefundRequest end");
                }
            } catch (HttpClientErrorException | HttpServerErrorException | NullPointerException e) {
                log.error("Failed connect to server: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Failed to reject: {}", e.getMessage(), e);
        }
    }

    public boolean paymentCompleteAndSaveWithKakao(KakaoApproveResponse response) {
        log.info("paymentCompleteAndSaveWithKakao start");
        try {
            Payment payment;
            if (response.getCard_info() != null) {
                payment = Payment.builder()
                        .aid(response.getAid())
                        .tid(response.getTid())
                        .cid(response.getCid())
                        .partner_order_id(response.getPartner_order_id())
                        .partner_user_id(response.getPartner_user_id())
                        .payment_method_type(response.getPayment_method_type())
                        .amount(new PaymentAmount(response.getAmount().getTotal(),
                                response.getAmount().getTax_free(),
                                response.getAmount().getTax(),
                                response.getAmount().getVat(),
                                response.getAmount().getPoint(),
                                response.getAmount().getDiscount(),
                                response.getAmount().getGreen_deposit()))
                        .card_info(new PaymentCardInfo(response.getCard_info().getPurchase_corp(),
                                response.getCard_info().getPurchase_corp_code(),
                                response.getCard_info().getIssuer_corp(),
                                response.getCard_info().getIssuer_corp_code(),
                                response.getCard_info().getKakaopay_purchase_corp(),
                                response.getCard_info().getKakaopay_purchase_corp_code(),
                                response.getCard_info().getKakaopay_issuer_corp(),
                                response.getCard_info().getKakaopay_issuer_corp_code(),
                                response.getCard_info().getBin(),
                                response.getCard_info().getCard_type(),
                                response.getCard_info().getInstall_month(),
                                response.getCard_info().getApproved_id(),
                                response.getCard_info().getCard_mid(),
                                response.getCard_info().getInterest_free_install(),
                                response.getCard_info().getCard_item_code()))
                        .item_name(response.getItem_name())
                        .quantity(response.getQuantity())
                        .created_at(response.getCreated_at())
                        .approved_at(response.getApproved_at())
                        .build();
                paymentRepository.save(payment);
                log.info("paymentCompleteAndSaveWithKakao end");
                return true;
            } else {
                payment = Payment.builder()
                        .aid(response.getAid())
                        .tid(response.getTid())
                        .cid(response.getCid())
                        .partner_order_id(response.getPartner_order_id())
                        .partner_user_id(response.getPartner_user_id())
                        .payment_method_type(response.getPayment_method_type())
                        .amount(new PaymentAmount(response.getAmount().getTotal(),
                                response.getAmount().getTax_free(),
                                response.getAmount().getTax(),
                                response.getAmount().getVat(),
                                response.getAmount().getPoint(),
                                response.getAmount().getDiscount(),
                                response.getAmount().getGreen_deposit()))
                        .item_name(response.getItem_name())
                        .quantity(response.getQuantity())
                        .created_at(response.getCreated_at())
                        .approved_at(response.getApproved_at())
                        .build();
                paymentRepository.save(payment);
                log.info("paymentCompleteAndSaveWithKakao end");
                return true;
            }
        } catch (NullPointerException e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return false;
        }
    }
    public String paymentTemporaryDataSaveAndReturnRedirectView (OrderProductRequest request){
        try {
            User user = authenticationService.findUserByUserToken(request.getUserToken());

            PaymentTemporarySaveRequest saveRequest = PaymentTemporarySaveRequest.builder()
                    .userToken(request.getUserToken())
                    .orderedPurchaserProfileRequest(request.getOrderedPurchaserProfileRequest())
                    .orderedProductOptionRequestList(request.getOrderedProductOptionRequestList())
                    .totalAmount(request.getTotalAmount())
                    .from(request.getFrom())
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

            KakaoPaymentRequest paymentRequest = KakaoPaymentRequest.builder()
                    .partner_order_id(user.getId() + itemName)
                    .partner_user_id(user.getId())
                    .item_name(itemName)
                    .quantity(quantity)
                    .total_amount(saveRequest.getTotalAmount())
                    .build();
            log.info("paymentRequest: " + paymentRequest);

            KakaoPaymentReadyResponse response = paymentRequest(paymentRequest);
            log.info("response: " + response);

            saveRequest.setTid(response.getTid());
            saveRequest.setPartnerOrderId(paymentRequest.getPartner_order_id());
            saveRequest.setPartnerUserId(paymentRequest.getPartner_user_id());
            redisService.paymentTemporarySaveData(user.getId(), saveRequest);
            log.info("saveRequest: " + saveRequest);

            String redirectView = response.getNext_redirect_pc_url();
            return redirectView;
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return null;
        }
    }
    public boolean isEveryOptionRefund(List<ProductOption> refundProductOptionList) {
        for (ProductOption productOption : refundProductOptionList) {
        }
        return false;
    }

    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + kakaoPaymentSecretsProvider.getAdminKey();

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }
}

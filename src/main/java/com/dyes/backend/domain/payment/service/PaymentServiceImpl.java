package com.dyes.backend.domain.payment.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRefundRequestForm;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRejectRequestForm;
import com.dyes.backend.domain.order.entity.OrderAmount;
import com.dyes.backend.domain.order.entity.OrderStatus;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
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
import com.dyes.backend.domain.payment.service.request.KakaoPaymentApprovalRequest;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRejectRequest;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRequest;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
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

import static com.dyes.backend.domain.order.entity.OrderStatus.CANCEL_PAYMENT;
import static com.dyes.backend.domain.order.entity.OrderStatus.PART_CANCEL_PAYMENT;

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
            final String vatAmount = String.valueOf(0);
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
    public boolean paymentApprovalRequest(KakaoPaymentApprovalRequest request) throws JsonProcessingException {
        log.info("paymentApprovalRequest start");

        final String userToken = request.getUserToken();
        User user = authenticationService.findUserByUserToken(userToken);

        final String userId = user.getId();

        PaymentTemporarySaveRequest saveRequest = redisService.getPaymentTemporarySaveData(userId);

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
                    redisService.deletePaymentTemporarySaveData(userId);
                    log.info("paymentApprovalRequest end");
                    return true;
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
        }
        return false;
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

    public boolean paymentRefundRequest(KakaoPaymentRefundRequestForm requestForm) {
        KakaoPaymentRefundRequest request = new KakaoPaymentRefundRequest(requestForm.getUserToken(), requestForm.getOrderId());
        List<KakaoPaymentRefundProductOptionRequest> requestList = requestForm.getRequestList();

        final String userToken = request.getUserToken();
        final Long orderId = request.getOrderId();

        try {
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                return false;
            }
            log.info("user: " + user.getId());
            Optional<ProductOrder> maybeOrder = orderRepository.findById(orderId);
            if (maybeOrder.isEmpty()) {
                return false;
            }
            ProductOrder order = maybeOrder.get();
            log.info("order: " + order.getId());

            final String cid = kakaoPaymentSecretsProvider.getCid();
            final String tid = order.getTid();
            final String refundUrl = kakaoPaymentSecretsProvider.getKakaoPaymentRefundUrl();
            Integer cancelAmount = 0;

            List<ProductOption> refundProductOptionList = new ArrayList<>();

            for (KakaoPaymentRefundProductOptionRequest optionRequest : requestList) {
                Long productOptionId = optionRequest.getProductOptionId();
                Optional<ProductOption> maybeProductOption = productOptionRepository.findById(productOptionId);
                if (maybeProductOption.isEmpty()) {
                    return false;
                }
                ProductOption productOption = maybeProductOption.get();
                log.info("productOption: " + productOption.getOptionName());

                cancelAmount += productOption.getOptionPrice().intValue();
                log.info("cancelAmount: " + cancelAmount);

                refundProductOptionList.add(productOption);
            }

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("cid", cid);
            parameters.add("tid", tid);
            parameters.add("cancel_amount", String.valueOf(cancelAmount));
            parameters.add("cancel_tax_free_amount", String.valueOf(cancelAmount));
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

            try {
                KakaoPaymentRefundResponse response = restTemplate.postForObject(refundUrl, requestEntity, KakaoPaymentRefundResponse.class);
                log.info("response: " + response);
                if (response.getApproved_cancel_amount() != null) {

                    OrderAmount orderAmount = order.getAmount();
                    Integer existingAmount = orderAmount.getTotalAmount();
                    Integer existingRefundAmount = orderAmount.getRefundedAmount();
                    orderAmount.setTotalAmount(existingAmount - cancelAmount);
                    orderAmount.setTotalAmount(existingRefundAmount + cancelAmount);
                    order.setAmount(orderAmount);

                    if (response.getStatus().equals(CANCEL_PAYMENT)) {
                        order.setOrderStatus(CANCEL_PAYMENT);
                    } else if (order.getAmount().getTotalAmount() == 0) {
                        order.setOrderStatus(CANCEL_PAYMENT);
                    } else {
                        order.setOrderStatus(PART_CANCEL_PAYMENT);
                    }

                    RefundedPayment refundedPayment = RefundedPayment.builder()
                            .aid(response.getAid())
                            .tid(response.getTid())
                            .user(user)
                            .approved_cancel_amount(response.getApproved_cancel_amount().getTotal())
                            .canceled_at(response.getCanceled_at())
                            .productOptionList(refundProductOptionList)
                            .build();
                    
                    orderRepository.save(order);
                    refundedPaymentRepository.save(refundedPayment);
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
                log.info("paymentCompleteAndSaveWithKakao end");
                return true;
            }
        } catch (NullPointerException e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return false;
        }
    }
    public RedirectView paymentTemporaryDataSaveAndReturnRedirectView (OrderProductRequest request){
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

            RedirectView redirectView = new RedirectView(response.getNext_redirect_pc_url());
            return redirectView;
        } catch (Exception e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return null;
        }
    }

    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + kakaoPaymentSecretsProvider.getAdminKey();

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }
}

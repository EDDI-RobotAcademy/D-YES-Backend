package com.dyes.backend.domain.payment.service;

import com.dyes.backend.domain.payment.service.request.KakaoPaymentRequest;
import com.dyes.backend.domain.payment.service.response.KakaoPaymentReadyResponse;
import com.dyes.backend.utility.provider.KakaoPaymentSecretsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    final private KakaoPaymentSecretsProvider provider;
    public KakaoPaymentReadyResponse paymentRequest(KakaoPaymentRequest request) {
        try{
            final String requestUrl = provider.getKakaoPaymentRequestUrl();

            final String cid = provider.getCid();
            final String partnerOrderId = request.getPartner_order_id();
            final String partnerUserId = request.getPartner_user_id();
            final String itemName = request.getItem_name();
            final String quantity = String.valueOf(request.getQuantity());
            final String totalAmount = String.valueOf(request.getTotal_amount());
            final String vatAmount = String.valueOf(request.getTax_free_amount());
            final String approvalUrl = provider.getKakaoPaymentApprovalUrl();
            final String cancelUrl = provider.getKakaoPaymentCancelUrl();
            final String failUrl = provider.getKakaoPaymentFailUrl();

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

            RestTemplate restTemplate = new RestTemplate();

            KakaoPaymentReadyResponse response = restTemplate.postForObject(requestUrl, requestEntity, KakaoPaymentReadyResponse.class);

            return response;
        } catch (HttpServerErrorException e) {
            log.error("Failed connect to server: {}", e.getMessage(), e);
            return null;
        }
    }
    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + provider.getAdminKey();

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }
}

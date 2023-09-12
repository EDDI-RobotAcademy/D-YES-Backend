package com.dyes.backend.utility.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource(value = "classpath:application.properties")
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaymentSecretsProvider {
    @Value("${kakao.payment.request.url}")
    private String kakaoPaymentRequestUrl;
    @Value("${kakao.payment.approval.url}")
    private String kakaoPaymentApprovalUrl;
    @Value("${kakao.payment.cancel.url}")
    private String kakaoPaymentCancelUrl;
    @Value("${kakao.payment.fail.url}")
    private String kakaoPaymentFailUrl;
    @Value("${kakao.payment.approve.url}")
    private String kakaoPaymentApproveUrl;
    @Value("${kakao.payment.cid}")
    private String cid;
    @Value("${kakao.payment.admin.key}")
    private String adminKey;
}

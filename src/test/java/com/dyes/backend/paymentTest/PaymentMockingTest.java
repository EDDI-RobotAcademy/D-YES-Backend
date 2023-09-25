package com.dyes.backend.paymentTest;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRefundRequestForm;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRejectRequestForm;
import com.dyes.backend.domain.order.entity.OrderAmount;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.order.service.user.request.*;
import com.dyes.backend.domain.order.service.user.response.KakaoPaymentRefundResponse;
import com.dyes.backend.domain.order.service.user.response.KakaoRefundApprovedCancelAmountRequest;
import com.dyes.backend.domain.payment.repository.PaymentRepository;
import com.dyes.backend.domain.payment.repository.RefundedPaymentRepository;
import com.dyes.backend.domain.payment.service.PaymentServiceImpl;
import com.dyes.backend.domain.payment.service.request.*;
import com.dyes.backend.domain.payment.service.response.KakaoApproveAmountResponse;
import com.dyes.backend.domain.payment.service.response.KakaoApproveCardInfoResponse;
import com.dyes.backend.domain.payment.service.response.KakaoApproveResponse;
import com.dyes.backend.domain.payment.service.response.KakaoPaymentReadyResponse;
import com.dyes.backend.domain.product.entity.Amount;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.entity.SaleStatus;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.user.entity.Active;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserType;
import com.dyes.backend.utility.provider.KakaoPaymentSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.dyes.backend.domain.order.entity.OrderStatus.CANCEL_PAYMENT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PaymentMockingTest {
    @Mock
    private KakaoPaymentSecretsProvider mockKakaoPaymentSecretsProvider;
    @Mock
    private RedisService mockRedisService;
    @Mock
    private PaymentRepository mockPaymentRepository;
    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private AuthenticationService mockAuthenticationService;
    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private RefundedPaymentRepository mockRefundedPaymentRepository;
    @Mock
    private OrderedProductRepository mockOrderedProductRepository;
    @InjectMocks
    private PaymentServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockService = new PaymentServiceImpl(
                mockRedisService,
                mockPaymentRepository,
                mockKakaoPaymentSecretsProvider,
                mockRestTemplate,
                mockProductOptionRepository,
                mockAuthenticationService,
                mockOrderRepository,
                mockRefundedPaymentRepository,
                mockOrderedProductRepository
        );
    }

    @Test
    @DisplayName("payment mocking test: kakao payment ready request")
    public void 사용자가_카카오로_결제를_요청합니다 () {

        KakaoPaymentRequest request = new KakaoPaymentRequest("partnerOrderId",
                "partnerUserId",
                "itemName", 1, 1
                );

        final String requestUrl = "requestUrl";
        final String cid = "cid";
        final String approvalUrl = "approvalUrl";
        final String cancelUrl = "cancelUrl";
        final String failUrl = "failUrl";
        final String adminKey = "adminKey";

        when(mockKakaoPaymentSecretsProvider.getKakaoPaymentRequestUrl()).thenReturn(requestUrl);
        when(mockKakaoPaymentSecretsProvider.getCid()).thenReturn(cid);
        when(mockKakaoPaymentSecretsProvider.getKakaoPaymentApprovalUrl()).thenReturn(approvalUrl);
        when(mockKakaoPaymentSecretsProvider.getKakaoPaymentCancelUrl()).thenReturn(cancelUrl);
        when(mockKakaoPaymentSecretsProvider.getKakaoPaymentFailUrl()).thenReturn(failUrl);
        when(mockKakaoPaymentSecretsProvider.getAdminKey()).thenReturn(adminKey);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", request.getPartner_order_id());
        parameters.add("partner_user_id", request.getPartner_user_id());
        parameters.add("item_name", request.getItem_name());
        parameters.add("quantity", request.getQuantity().toString());
        parameters.add("total_amount", request.getTotal_amount().toString());
        parameters.add("vat_amount", "1");
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", approvalUrl);
        parameters.add("cancel_url", cancelUrl);
        parameters.add("fail_url", failUrl);

        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + adminKey;
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        KakaoPaymentReadyResponse response = new KakaoPaymentReadyResponse("tid",
                "mobile_url",
                "pc_url",
                "created_at"
                );
        when(mockRestTemplate.postForObject(eq(requestUrl), any(HttpEntity.class), eq(KakaoPaymentReadyResponse.class))).thenReturn(response);

        KakaoPaymentReadyResponse result = mockService.paymentRequest(request);
        assertTrue(result != null);
    }
    @Test
    @DisplayName("payment mocking test: kakao payment approval request")
    public void 사용자가_결제를_확정합니다() throws JsonProcessingException {

        final String userToken = "userToken";
        final String pgToken = "pgToken";
        KakaoPaymentApprovalRequest request = new KakaoPaymentApprovalRequest(userToken, pgToken);
        User user = new User("id", "access token", "refresh token", Active.YES, UserType.GOOGLE);

        when(mockAuthenticationService.findUserByUserToken(request.getUserToken())).thenReturn(user);

        final String orderedPurchaserName = "orderedPurchaserName";
        final String orderedPurchaserContactNumber = "orderedPurchaserContactNumber";
        final String orderedPurchaserEmail = "orderedPurchaserEmail";
        final String orderedPurchaserAddress = "orderedPurchaserAddress";
        final String orderedPurchaserZipCode = "orderedPurchaserZipCode";
        final String orderedPurchaserAddressDetail = "orderedPurchaserAddressDetail";

        OrderedPurchaserProfileRequest profileRequest = new OrderedPurchaserProfileRequest(
                orderedPurchaserName,
                orderedPurchaserContactNumber,
                orderedPurchaserEmail,
                orderedPurchaserAddress,
                orderedPurchaserZipCode,
                orderedPurchaserAddressDetail
                );

        final Long productOptionId = 1L;
        final int productOptionCount = 1;

        OrderedProductOptionRequest optionRequest = new OrderedProductOptionRequest(productOptionId, productOptionCount);

        PaymentTemporarySaveRequest saveRequest = new PaymentTemporarySaveRequest(
                request.getUserToken(), profileRequest, List.of(optionRequest),
                1, "cart", "tid", "partnerOrderId",
                "partnerUserId");

        when(mockRedisService.getPaymentTemporarySaveData(user.getId())).thenReturn(saveRequest);

        final String tid = saveRequest.getTid();
        final String cid = "cid";
        final String approveUrl = "approveUrl";
        final String partnerOrderId = saveRequest.getPartnerOrderId();
        final String partnerUserId = saveRequest.getPartnerUserId();
        final String adminKey = "adminKey";

        when(mockKakaoPaymentSecretsProvider.getCid()).thenReturn(cid);
        when(mockKakaoPaymentSecretsProvider.getKakaoPaymentApproveUrl()).thenReturn(approveUrl);
        when(mockKakaoPaymentSecretsProvider.getAdminKey()).thenReturn(adminKey);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", tid);
        parameters.add("partner_order_id", partnerOrderId);
        parameters.add("partner_user_id", partnerUserId);
        parameters.add("pg_token", pgToken);

        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + adminKey;
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, httpHeaders);

        LocalDate created_at = LocalDate.ofEpochDay(1);
        LocalDate approved_at = LocalDate.ofEpochDay(1);

        KakaoApproveResponse approveResponse = new KakaoApproveResponse("aid", tid, cid,
                partnerOrderId, partnerUserId, "paymentMethod",
                new KakaoApproveAmountResponse(1, 1, 1, 1, 1, 1,1),
                new KakaoApproveCardInfoResponse("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "!", "1"),
                "1", 1, created_at, approved_at
                );
        when(mockRestTemplate.postForObject(approveUrl, requestEntity, KakaoApproveResponse.class)).thenReturn(approveResponse);

        PaymentTemporarySaveRequest result = mockService.paymentApprovalRequest(request);
        assertTrue(result != null);
    }
    @Test
    @DisplayName("payment mocking test: kakao payment redirectview return")
    public void 사용자가_결제를_요청하면_결제_진행_리다이렉트_페이지를_리턴해줍니다() throws JsonProcessingException {
        OrderedPurchaserProfileRequest profileRequest = new OrderedPurchaserProfileRequest("이름", "전화번호", "이메일", "주소", "지번", "주소디테일");
        OrderedProductOptionRequest optionRequest = new OrderedProductOptionRequest(1L, 1);
        final String userToken = "userToken";
        final int totalAmount = 1;
        final String from = "cart";
        OrderProductRequest request = new OrderProductRequest(userToken, profileRequest, List.of(optionRequest), totalAmount, from);

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        PaymentTemporarySaveRequest saveRequest = PaymentTemporarySaveRequest.builder()
                .userToken(request.getUserToken())
                .orderedPurchaserProfileRequest(request.getOrderedPurchaserProfileRequest())
                .orderedProductOptionRequestList(request.getOrderedProductOptionRequestList())
                .totalAmount(request.getTotalAmount())
                .from(request.getFrom())
                .build();

        ProductOption productOption = new ProductOption(1L, "optionName", 1L, 1, new Amount(), new Product(), SaleStatus.AVAILABLE);
        when(mockProductOptionRepository.findById(saveRequest.getOrderedProductOptionRequestList().get(0).getProductOptionId())).thenReturn(Optional.of(productOption));

        final String requestUrl = "requestUrl";
        final String adminKey = "adminKey";

        when(mockKakaoPaymentSecretsProvider.getKakaoPaymentRequestUrl()).thenReturn(requestUrl);
        when(mockKakaoPaymentSecretsProvider.getAdminKey()).thenReturn(adminKey);

        KakaoPaymentReadyResponse response = new KakaoPaymentReadyResponse("tid",
                "mobile_url",
                "pc_url",
                "created_at"
        );

        when(mockRestTemplate.postForObject(eq(requestUrl), any(HttpEntity.class), eq(KakaoPaymentReadyResponse.class))).thenReturn(response);

        String result = mockService.paymentTemporaryDataSaveAndReturnRedirectView(request);
        assertTrue(result.equals(response.getNext_redirect_pc_url()));
    }
    @Test
    @DisplayName("payment mocking test: kakao payment reject")
    public void 사용자가_결제를_취소하거나_실패하였습니다() {
        final String userToken = "userToken";
        KakaoPaymentRejectRequestForm requestForm = new KakaoPaymentRejectRequestForm(userToken);
        KakaoPaymentRejectRequest request = new KakaoPaymentRejectRequest(requestForm.getUserToken());

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(request.getUserToken())).thenReturn(user);

        boolean result = mockService.paymentRejectWithKakao(requestForm);
        assertTrue(result);
    }
    @Test
    @DisplayName("payment mocking test: kakao payment refund")
    public void 사용자가_확정된_결제를_환불합니다() {
        final String userToken = "userToken";
        final Long orderId = 1L;
        final Long productOptionId = 1L;
        final String refundReason = "환불 사유";
        KakaoPaymentRefundProductOptionRequest optionRequest = new KakaoPaymentRefundProductOptionRequest(productOptionId);
        KakaoPaymentRefundOrderAndTokenAndReasonRequest orderAndTokenAndReasonRequest = new KakaoPaymentRefundOrderAndTokenAndReasonRequest(userToken, orderId, refundReason);
        KakaoPaymentRefundRequestForm requestForm = new KakaoPaymentRefundRequestForm(orderAndTokenAndReasonRequest, List.of(optionRequest));

        User user = new User();
        user.setId("id");
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        final String tid = "tid";
        ProductOrder order = new ProductOrder();
        order.setId(1L);
        order.setTid(tid);

        OrderAmount orderAmount = new OrderAmount(100, 100);
        order.setAmount(orderAmount);
        when(mockOrderRepository.findById(orderAndTokenAndReasonRequest.getOrderId())).thenReturn(Optional.of(order));

        final String refundUrl = "refund_url";
        final String cid = "cid";

        when(mockKakaoPaymentSecretsProvider.getCid()).thenReturn(cid);
        when(mockKakaoPaymentSecretsProvider.getKakaoPaymentRefundUrl()).thenReturn(refundUrl);

        final Integer cancel_amount = 1;

        ProductOption productOption = new ProductOption();
        productOption.setOptionName("option_name");
        productOption.setOptionPrice(1L);
        when(mockProductOptionRepository.findById(optionRequest.getProductOptionId())).thenReturn(Optional.of(productOption));

        KakaoPaymentRefundResponse refundResponse = new KakaoPaymentRefundResponse();
        KakaoRefundApprovedCancelAmountRequest approvedCancelAmountRequest = new KakaoRefundApprovedCancelAmountRequest();
        approvedCancelAmountRequest.setTotal(cancel_amount);
        refundResponse.setTid(tid);
        refundResponse.setApproved_cancel_amount(approvedCancelAmountRequest);
        refundResponse.setStatus(String.valueOf(CANCEL_PAYMENT));

        final String adminKey = "adminKey";
        when(mockKakaoPaymentSecretsProvider.getAdminKey()).thenReturn(adminKey);

        when(mockRestTemplate.postForObject(eq(refundUrl), any(HttpEntity.class), eq(KakaoPaymentRefundResponse.class))).thenReturn(refundResponse);

        boolean result = mockService.paymentRefundRequest(orderAndTokenAndReasonRequest, List.of(optionRequest));
        assertTrue(result);
    }
}

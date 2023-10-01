package com.dyes.backend.orderTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.cart.repository.CartRepository;
import com.dyes.backend.domain.cart.repository.ContainProductOptionRepository;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.delivery.entity.Delivery;
import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import com.dyes.backend.domain.delivery.repository.DeliveryRepository;
import com.dyes.backend.domain.event.repository.EventOrderRepository;
import com.dyes.backend.domain.event.repository.EventProductRepository;
import com.dyes.backend.domain.event.repository.EventPurchaseCountRepository;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderedProductChangeStatusRequestForm;
import com.dyes.backend.domain.order.entity.OrderAmount;
import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.OrderedPurchaserProfile;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.order.repository.OrderedProductRepository;
import com.dyes.backend.domain.order.repository.OrderedPurchaserProfileRepository;
import com.dyes.backend.domain.order.service.OrderServiceImpl;
import com.dyes.backend.domain.order.service.admin.response.form.OrderDetailDataResponseForAdminForm;
import com.dyes.backend.domain.order.service.user.request.OrderConfirmProductRequest;
import com.dyes.backend.domain.order.service.user.request.OrderConfirmRequest;
import com.dyes.backend.domain.order.service.user.request.OrderedProductOptionRequest;
import com.dyes.backend.domain.order.service.user.request.OrderedPurchaserProfileRequest;
import com.dyes.backend.domain.order.service.user.response.OrderConfirmProductResponse;
import com.dyes.backend.domain.order.service.user.response.OrderConfirmUserResponse;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.payment.entity.Payment;
import com.dyes.backend.domain.payment.entity.PaymentAmount;
import com.dyes.backend.domain.payment.repository.PaymentRepository;
import com.dyes.backend.domain.payment.service.PaymentService;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
import com.dyes.backend.domain.product.entity.*;
import com.dyes.backend.domain.product.repository.ProductMainImageRepository;
import com.dyes.backend.domain.product.repository.ProductOptionRepository;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.review.repository.ReviewRepository;
import com.dyes.backend.domain.user.entity.*;
import com.dyes.backend.domain.user.repository.AddressBookRepository;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dyes.backend.domain.farm.entity.ProduceType.ONION;
import static com.dyes.backend.domain.user.entity.AddressBookOption.DEFAULT_OPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest

public class OrderMockingTest {
    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private ContainProductOptionRepository mockContainProductOptionRepository;
    @Mock
    private CartService mockCartService;
    @Mock
    private CartRepository mockCartRepository;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private AuthenticationService mockAuthenticationService;
    @Mock
    private UserProfileRepository mockUserProfileRepository;
    @Mock
    private AddressBookRepository mockAddressBookRepository;
    @Mock
    private ProductMainImageRepository mockProductMainImageRepository;
    @Mock
    private OrderedProductRepository mockOrderedProductRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private RedisService mockRedisService;
    @Mock
    private PaymentService mockPaymentService;
    @Mock
    private OrderedPurchaserProfileRepository mockOrderedPurchaserProfileRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private PaymentRepository mockPaymentRepository;
    @Mock
    private ReviewRepository mockReviewRepository;
    @Mock
    private EventProductRepository mockEventProductRepository;
    @Mock
    private EventOrderRepository mockEventOrderRepository;
    @Mock
    private AdminService mockAdminService;
    @Mock
    private EventPurchaseCountRepository mockEventPurchaseCountRepository;

    @InjectMocks
    private OrderServiceImpl mockOrderService;
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockOrderService = new OrderServiceImpl(
                mockOrderRepository,
                mockProductOptionRepository,
                mockContainProductOptionRepository,
                mockUserProfileRepository,
                mockAddressBookRepository,
                mockProductMainImageRepository,
                mockOrderedProductRepository,
                mockOrderedPurchaserProfileRepository,
                deliveryRepository,
                mockCartService,
                mockPaymentService,
                mockAuthenticationService,
                mockRedisService,
                mockProductRepository,
                mockPaymentRepository,
                mockReviewRepository,
                mockEventProductRepository,
                mockEventOrderRepository,
                mockEventPurchaseCountRepository,
                mockAdminService
                );
    }
    @Test
    @DisplayName("order mocking test: order product in cart")
    public void 사용자가_장바구니에서_물품을_주문합니다 () {
        final String userToken = "google 유저";
        final String accessToken = "엑세스 토큰";
        final int totalAmount = 1;
        PaymentTemporarySaveRequest saveRequest = new PaymentTemporarySaveRequest(
                userToken, new OrderedPurchaserProfileRequest("구매자 이름", "전화 번호", "이메일",
                        "주소", "코드", "주소 디테일"),
                List.of(new OrderedProductOptionRequest(1L, 1)), 1, "from", "tid", "part", "or");
        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductOptionId(saveRequest.getOrderedProductOptionRequestList().get(0).getProductOptionId());
        saveRequest.setUserToken(userToken);
        when(mockRedisService.getAccessToken(userToken)).thenReturn(accessToken);
        User user = new User();
        when(mockUserRepository.findByAccessToken(accessToken)).thenReturn(Optional.of(user));

        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);
        Cart cart = new Cart(1L, user);
        when(mockCartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(mockCartService.cartCheckFromUserToken(userToken)).thenReturn(cart);
        ContainProductOption containProductOption = new ContainProductOption();
        containProductOption.setId(1L);
        containProductOption.setCart(cart);
        when(mockContainProductOptionRepository.findAllByCart(cart)).thenReturn(List.of(containProductOption));

        mockOrderService.orderProduct(saveRequest);
    }
    @Test
    @DisplayName("order mocking test: confirm order")
    public void 사용자가_주문하기_전에_주문_확인을_할_수_있습니다 () {
        final String userToken = "google 유저";
        OrderConfirmProductRequest productRequest = new OrderConfirmProductRequest(1L);
        OrderConfirmRequestForm requestForm = new OrderConfirmRequestForm(userToken, List.of(productRequest));
        OrderConfirmRequest request = new OrderConfirmRequest(requestForm.getUserToken());

        User user = new User("1", "엑세스토큰", "리프래시 토큰", Active.YES, UserType.GOOGLE);
        when(mockAuthenticationService.findUserByUserToken(request.getUserToken())).thenReturn(user);
        UserProfile userProfile = new UserProfile("아이디", "닉네임", "이메일", "프로필 사진", "전화번호", new Address(), user);
        when(mockUserProfileRepository.findByUser(user)).thenReturn(Optional.of(userProfile));

        AddressBook addressBook = new AddressBook(1L, DEFAULT_OPTION, "홍길동", "010-1111-1111", new Address(), user);
        List<AddressBook> addressBookList = new ArrayList<>();
        addressBookList.add(addressBook);
        when(mockAddressBookRepository.findAllByUser(user)).thenReturn(addressBookList);

        Cart cart = new Cart(1L, user);
        when(mockCartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        Product product = new Product(1L, "상품 이름", "상세 설명", CultivationMethod.ORGANIC, ONION, SaleStatus.AVAILABLE, new Farm(), MaybeEventProduct.NO);
        ProductOption productOption = new ProductOption(1L, "옵션이름", 1L, 1, new Amount(), product, SaleStatus.AVAILABLE);
        ContainProductOption containProductOption = new ContainProductOption(1L, cart, "상품 이름", 1L, "상품 이미지", 1L, "옵션 이름", 1L, 1);
        when(mockContainProductOptionRepository.findAllByCart(cart)).thenReturn(List.of(containProductOption));
        when(mockProductOptionRepository.findByIdWithProduct(containProductOption.getId())).thenReturn(Optional.of(productOption));
        ProductMainImage mainImage = new ProductMainImage(1L, "메인 이미지", product);
        when(mockProductMainImageRepository.findByProductId(product.getId())).thenReturn(Optional.of(mainImage));

        OrderConfirmUserResponse userResponse = OrderConfirmUserResponse.builder()
                .email(userProfile.getEmail())
                .contactNumber(userProfile.getContactNumber())
                .address(addressBook.getAddress().getAddress())
                .zipCode(addressBook.getAddress().getZipCode())
                .addressDetail(addressBook.getAddress().getAddressDetail())
                .build();

        OrderConfirmProductResponse productResponse = OrderConfirmProductResponse.builder()
                .productName(productOption.getProduct().getProductName())
                .optionId(productOption.getId())
                .optionPrice(productOption.getOptionPrice())
                .productMainImage(mainImage.getMainImg())
                .value(productOption.getAmount().getValue())
                .unit(productOption.getAmount().getUnit())
                .build();
        OrderConfirmResponseFormForUser responseForm = new OrderConfirmResponseFormForUser(userResponse, List.of(productResponse));

        OrderConfirmResponseFormForUser actual = mockOrderService.orderConfirm(requestForm);

        assertEquals(responseForm.getUserResponse().getEmail(), actual.getUserResponse().getEmail());
    }
    @Test
    @DisplayName("order mocking test: admin view order data")
    public void 관리자가_주문의_상세_데이터를_볼_수_있습니다() {
        final Long orderId = 1L;

        ProductOrder order = new ProductOrder();
        order.setDelivery(new Delivery(1L, DeliveryStatus.DELIVERED, LocalDate.now(), LocalDate.now()));
        when(mockOrderRepository.findByStringIdWithDelivery(orderId)).thenReturn(Optional.of(order));
        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductOrder(order);
        when(mockOrderedProductRepository.findAllByProductOrder(order)).thenReturn(List.of(orderedProduct));
        Payment payment = new Payment();
        PaymentAmount amount = new PaymentAmount();
        payment.setAmount(amount);
        when(mockPaymentRepository.findByProductOrder(order)).thenReturn(Optional.of(payment));
        OrderedPurchaserProfile orderedPurchaserProfile = new OrderedPurchaserProfile();
        Address address = new Address();
        orderedPurchaserProfile.setOrderedPurchaseProfileAddress(address);
        when(mockOrderedPurchaserProfileRepository.findByProductOrder(order)).thenReturn(Optional.of(orderedPurchaserProfile));
        ProductOption productOption = new ProductOption();
        when(mockProductOptionRepository.findById(orderedProduct.getProductOptionId())).thenReturn(Optional.of(productOption));

        OrderDetailDataResponseForAdminForm result = mockOrderService.orderDetailDataCombineForAdmin(orderId);
        assertTrue(result != null);
    }

    @Test
    @DisplayName("payment mocking test: kakao payment waiting refund")
    public void 사용자가_환불을_신청하면_환불대기_상태가_됩니다() {
        final Long orderId = 1L;
        final Long productOptionId = 1L;
        final String userToken = "userToken";
        final String refundReason = "refundReason";

        OrderedProductChangeStatusRequestForm requestForm = new OrderedProductChangeStatusRequestForm(userToken, orderId, List.of(productOptionId), refundReason);

        Admin admin = new Admin();
        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(admin);

        ProductOrder order = new ProductOrder();
        order.setId(1L);

        OrderAmount orderAmount = new OrderAmount(100, 100);
        order.setAmount(orderAmount);
        when(mockOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductOptionId(1L);
        when(mockOrderedProductRepository.findAllByProductOrder(order)).thenReturn(List.of(orderedProduct));

        boolean result = mockOrderService.orderedProductWaitingRefund(requestForm);
        assertTrue(result);
    }
}

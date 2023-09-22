package com.dyes.backend.eventTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.event.controller.form.EventProductRegisterRequestForm;
import com.dyes.backend.domain.event.entity.EventDeadLine;
import com.dyes.backend.domain.event.entity.EventProduct;
import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import com.dyes.backend.domain.event.repository.EventDeadLineRepository;
import com.dyes.backend.domain.event.repository.EventProductRepository;
import com.dyes.backend.domain.event.repository.EventPurchaseCountRepository;
import com.dyes.backend.domain.event.service.EventServiceImpl;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.repository.*;
import com.dyes.backend.domain.product.service.admin.AdminProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest

public class EventMockingTest {
    @Mock
    private EventProductRepository mockEventProductRepository;
    @Mock
    private EventPurchaseCountRepository mockEventPurchaseCountRepository;
    @Mock
    private EventDeadLineRepository mockEventDeadLineRepository;
    @Mock
    private AdminService mockAdminService;
    @Mock
    private FarmRepository mockFarmRepository;
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private ProductManagementRepository mockProductManagementRepository;
    @Mock
    private ProductOptionRepository mockProductOptionRepository;
    @Mock
    private ProductMainImageRepository mockProductMainImageRepository;
    @Mock
    private ProductDetailImagesRepository mockProductDetailImagesRepository;
    @InjectMocks
    private EventServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new EventServiceImpl(
                mockEventProductRepository,
                mockEventPurchaseCountRepository,
                mockEventDeadLineRepository,
                mockAdminService,
                mockFarmRepository,
                mockProductRepository,
                mockProductManagementRepository,
                mockProductOptionRepository,
                mockProductMainImageRepository,
                mockProductDetailImagesRepository
        );
    }
    @Test
    @DisplayName("event mocking test: evnet product register")
    public void 관리자가_공동구매_물품을_등록합니다() {
        final String detailImage = "상세이미지";

        EventProductRegisterRequest request = new EventProductRegisterRequest();
        EventProductRegisterDeadLineRequest deadLineRequest = new EventProductRegisterDeadLineRequest();
        EventProductRegisterPurchaseCountRequest countRequest = new EventProductRegisterPurchaseCountRequest();

        final String userToken = request.getUserToken();
        final String farmName = request.getFarmName();

        request.setDetailImgs(List.of(detailImage));

        Admin admin = new Admin();
        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(admin);
        Farm farm = new Farm();
        when(mockFarmRepository.findByFarmName(farmName)).thenReturn(Optional.of(farm));

        boolean result = mockService.eventProductRegister(request, deadLineRequest, countRequest);
        assertTrue(result);
    }
}

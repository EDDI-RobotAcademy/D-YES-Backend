package com.dyes.backend.farmTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.farm.repository.FarmOperationRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.service.FarmServiceImpl;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.user.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FarmMockingTest {
    @Mock
    private FarmRepository mockFarmRepository;
    @Mock
    private FarmOperationRepository mockFarmOperationRepository;
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private AdminService mockAdminService;
    @InjectMocks
    private FarmServiceImpl farmService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        farmService = new FarmServiceImpl(
                mockFarmRepository,
                mockFarmOperationRepository,
                mockProductRepository,
                mockAdminService);
    }

    @Test
    @DisplayName("farm mocking test: farmRegister")
    public void 관리자가_농가를_등록합니다 () {
        List<ProduceType> produceTypeList = new ArrayList<>();
        FarmRegisterRequestForm requestForm
                = new FarmRegisterRequestForm("mainadmin-kfweg", "투투농가", "070-1234-5678",
                "서울특별시 강남구 테헤란로14길 6", "06234", "6층", "mainImage", "introduction", produceTypeList,
                "(주)투투농장", "123-45-67891", "정다운", "010-1234-5678");
        when(mockFarmRepository.findByFarmName("투투농가")).thenReturn(Optional.empty());
        when(mockAdminService.findAdminByUserToken("mainadmin-kfweg")).thenReturn(new Admin());

        boolean result = farmService.farmRegister(requestForm);
        assertTrue(result);

        verify(mockFarmRepository, times(1)).save(any());
        verify(mockFarmOperationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("farm mocking test: searchFarmList")
    public void 관리자가_농가목록을_조회합니다 () {
        Farm farm1 = Farm.builder()
                .farmName("투투농원1")
                .farmAddress(new Address())
                .build();
        Farm farm2 = Farm.builder()
                .farmName("투투농원2")
                .farmAddress(new Address())
                .build();
        List<Farm> farmList = new ArrayList<>();
        farmList.add(farm1);
        farmList.add(farm2);

        when(mockFarmRepository.findAll()).thenReturn(farmList);

        List<FarmInfoListResponse> result = farmService.searchFarmList();
        assertEquals(result.get(0).getFarmName(), "투투농원1");
        assertEquals(result.get(1).getFarmName(), "투투농원2");
    }
}

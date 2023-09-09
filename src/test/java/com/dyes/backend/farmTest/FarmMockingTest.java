package com.dyes.backend.farmTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.farm.controller.form.FarmDeleteRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmModifyRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.*;
import com.dyes.backend.domain.farm.service.FarmServiceImpl;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import com.dyes.backend.domain.farm.service.response.FarmInfoReadResponse;
import com.dyes.backend.domain.product.entity.Product;
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
    private FarmBusinessInfoRepository mockFarmBusinessInfoRepository;
    @Mock
    private FarmCustomerServiceInfoRepository mockFarmCustomerServiceInfoRepository;
    @Mock
    private FarmIntroductionInfoRepository mockFarmIntroductionInfoRepository;
    @Mock
    private FarmRepresentativeInfoRepository mockFarmRepresentativeInfoRepository;
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
                mockFarmBusinessInfoRepository,
                mockFarmCustomerServiceInfoRepository,
                mockFarmIntroductionInfoRepository,
                mockFarmRepresentativeInfoRepository,
                mockProductRepository,
                mockAdminService);
    }

    @Test
    @DisplayName("farm mocking test: farmRegister")
    public void 관리자가_농가를_등록합니다() {
        List<ProduceType> produceTypeList = new ArrayList<>();
        FarmRegisterRequestForm requestForm
                = new FarmRegisterRequestForm("mainadmin-kfweg", "투투농가", "070-1234-5678",
                "서울특별시 강남구 테헤란로14길 6", "06234", "6층", "mainImage", "introduction", produceTypeList,
                "(주)투투농장", "123-45-67891", "정다운", "010-1234-5678");
        when(mockFarmRepository.findByFarmName("투투농가")).thenReturn(Optional.empty());
        when(mockAdminService.findAdminByUserToken("mainadmin-kfweg")).thenReturn(new Admin());

        boolean result = farmService.registerFarm(requestForm);
        assertTrue(result);

        verify(mockFarmRepository, times(1)).save(any());
        verify(mockFarmBusinessInfoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("farm mocking test: searchFarmList")
    public void 관리자가_농가목록을_조회합니다() {
        Farm farm1 = Farm.builder()
                .farmName("투투농원1")
                .build();
        Farm farm2 = Farm.builder()
                .farmName("투투농원2")
                .build();
        FarmCustomerServiceInfo farmCustomerServiceInfo1 = FarmCustomerServiceInfo.builder()
                .id(farm1.getId())
                .farm(farm1)
                .build();
        FarmCustomerServiceInfo farmCustomerServiceInfo2 = FarmCustomerServiceInfo.builder()
                .id(farm2.getId())
                .farm(farm2)
                .build();
        List<Farm> farmList = new ArrayList<>();
        farmList.add(farm1);
        farmList.add(farm2);

        when(mockFarmRepository.findAll()).thenReturn(farmList);
        when(mockFarmCustomerServiceInfoRepository.findByFarm(farm1)).thenReturn(farmCustomerServiceInfo1);
        when(mockFarmCustomerServiceInfoRepository.findByFarm(farm2)).thenReturn(farmCustomerServiceInfo2);

        List<FarmInfoListResponse> result = farmService.getFarmList();
        assertEquals(result.get(0).getFarmName(), "투투농원1");
        assertEquals(result.get(1).getFarmName(), "투투농원2");
    }

    @Test
    @DisplayName("farm mocking test: deleteFarm")
    public void 관리자가_농가를_삭제합니다() {
        final Long farmId = 1L;
        final String userToken = "mainadmin";
        final FarmDeleteRequestForm deleteForm = new FarmDeleteRequestForm(userToken);
        final List<Product> productList = new ArrayList<>();
        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(new Admin());
        when(mockFarmRepository.findById(farmId)).thenReturn(Optional.of(new Farm()));
        when(mockProductRepository.findAllByFarm(new Farm())).thenReturn(productList);
        when(mockFarmBusinessInfoRepository.findByFarm(new Farm())).thenReturn(new FarmBusinessInfo());

        farmService.deleteFarm(farmId, deleteForm);

        verify(mockFarmRepository, times(1)).delete(any());
        verify(mockFarmBusinessInfoRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("farm mocking test: readFarmInfo")
    public void 관리자가_농가정보를_확인합니다() {
        final Long farmId = 1L;
        Farm farm = Farm.builder()
                .farmName("투투농가")
                .build();

        FarmCustomerServiceInfo farmCustomerServiceInfo = FarmCustomerServiceInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        FarmIntroductionInfo farmIntroductionInfo = FarmIntroductionInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        FarmRepresentativeInfo farmRepresentativeInfo = FarmRepresentativeInfo.builder()
                .id(farm.getId())
                .farm(farm)
                .build();

        FarmBusinessInfo farmBusinessInfo = FarmBusinessInfo.builder()
                .businessName("(주)투투농가")
                .businessNumber("123-45-67890")
                .build();
        when(mockFarmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(mockFarmBusinessInfoRepository.findByFarm(farm)).thenReturn(farmBusinessInfo);
        when(mockFarmCustomerServiceInfoRepository.findByFarm(farm)).thenReturn(farmCustomerServiceInfo);
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);
        when(mockFarmRepresentativeInfoRepository.findByFarm(farm)).thenReturn(farmRepresentativeInfo);

        FarmInfoReadResponse result = farmService.readFarm(farmId);
        assertEquals(result.getFarmInfoResponseForm().getFarmName(), "투투농가");
        assertEquals(result.getFarmOperationInfoResponseForm().getBusinessName(), "(주)투투농가");
        assertEquals(result.getFarmOperationInfoResponseForm().getBusinessNumber(), "123-45-67890");
    }

    @Test
    @DisplayName("farm mocking test: farmModify")
    public void 관리자가_농가정보를_수정합니다() {
        final Long farmId = 1L;
        final String userToken = "mainadmin";
        Farm farm = Farm.builder()
                .farmName("투투농가")
                .build();

        FarmCustomerServiceInfo farmCustomerServiceInfo = FarmCustomerServiceInfo.builder()
                .csContactNumber("070-1234-5678")
                .farmAddress(new Address())
                .farm(farm)
                .build();

        FarmIntroductionInfo farmIntroductionInfo = FarmIntroductionInfo.builder()
                .mainImage("메인이미지")
                .introduction("한줄소개")
                .produceTypes(new ArrayList<>())
                .farm(farm)
                .build();

        when(mockAdminService.findAdminByUserToken(userToken)).thenReturn(new Admin());
        when(mockFarmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(mockFarmCustomerServiceInfoRepository.findByFarm(farm)).thenReturn(farmCustomerServiceInfo);
        when(mockFarmIntroductionInfoRepository.findByFarm(farm)).thenReturn(farmIntroductionInfo);

        FarmModifyRequestForm modifyForm = new FarmModifyRequestForm(userToken, "070-1111-1111", "수정된메인이미지", "수정된한줄소개", new ArrayList<>());
        Boolean result = farmService.modifyFarm(farmId, modifyForm);

        assertTrue(result);
        assertEquals("070-1111-1111", farmCustomerServiceInfo.getCsContactNumber());
        assertEquals("수정된메인이미지", farmIntroductionInfo.getMainImage());
        assertEquals("수정된한줄소개", farmIntroductionInfo.getIntroduction());
    }
}

package com.dyes.backend.farmTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.farm.repository.FarmOperationRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.service.FarmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

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
    private AdminService mockAdminService;
    @InjectMocks
    private FarmServiceImpl farmService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        farmService = new FarmServiceImpl(
                mockFarmRepository,
                mockFarmOperationRepository,
                mockAdminService);
    }

    @Test
    @DisplayName("farm mocking test: farm register")
    public void 관리자가_농가를_등록합니다 () {
        List<ProduceType> produceTypeList = new ArrayList<>();
        FarmRegisterRequestForm requestForm
                = new FarmRegisterRequestForm("mainadmin-kfweg", "투투농가", "070-1234-5678",
                "서울특별시 강남구 테헤란로14길 6", "06234", "6층", "mainImage", "introduction", produceTypeList,
                "(주)투투농장", "123-45-67891", "정다운", "010-1234-5678");
        when(mockAdminService.findAdminByUserToken("mainadmin-kfweg")).thenReturn(new Admin());

        boolean result = farmService.farmRegister(requestForm);
        assertTrue(result);

        verify(mockFarmRepository, times(1)).save(any());
        verify(mockFarmOperationRepository, times(1)).save(any());
    }
}

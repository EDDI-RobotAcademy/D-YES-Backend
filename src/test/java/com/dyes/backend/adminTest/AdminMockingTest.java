package com.dyes.backend.adminTest;

import com.dyes.backend.domain.admin.controller.form.AdminRegisterRequestForm;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.AdminServiceImpl;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminMockingTest {
    @Mock
    private AdminRepository mockAdminRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private AdminServiceImpl mockAdminService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockAdminService = new AdminServiceImpl(
                mockAdminRepository,
                mockUserRepository,
                userService);
    }

    @Test
    @DisplayName("admin mocking test: normal admin register")
    public void 메인관리자가_일반관리자를_등록합니다 () {
        final String userId = "1234567890";
        final String userToken = "mainadmin-wwlkfej-4weth3eggwhg";
        final String name = "normaladmin";
        User user = User.builder()
                .id(userId)
                .build();

        when(mockUserRepository.findByStringId(userId)).thenReturn(Optional.of(user));

        AdminRegisterRequestForm requestForm
                = new AdminRegisterRequestForm(userToken, userId, name);

        boolean result = mockAdminService.adminRegister(requestForm);
        assertTrue(result);

        verify(mockAdminRepository, times(1)).save(any());
        verify(mockUserRepository, times(1)).findByStringId(any());
    }
}

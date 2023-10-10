package com.dyes.backend.domain.admin.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.request.AdminRegisterRequest;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.dyes.backend.domain.admin.entity.RoleType.NORMAL_ADMIN;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    final private AdminRepository adminRepository;
    final private UserRepository userRepository;
    final private AuthenticationService authenticationService;

    // 일반 관리자 등록
    @Override
    public boolean adminRegister(UserAuthenticationRequest userAuthenticationRequest, AdminRegisterRequest adminRegisterRequest) {
        log.info("Starting administrator registration");

        final String mainAdminUserToken = userAuthenticationRequest.getUserToken();
        if (!mainAdminUserToken.contains("mainadmin")) {
            log.warn("Registration Denied: Main Admin Access Only");
            return false;
        }

        final String registerRequestUserId = adminRegisterRequest.getId();
        try {
            Optional<User> maybeUser = userRepository.findByStringId(registerRequestUserId);

            if (maybeUser.isEmpty()) {
                log.warn("Can not find user: id - {}", registerRequestUserId);
                return false;
            }
            final String registerRequestUserName = adminRegisterRequest.getName();
            final User user = maybeUser.get();
            final Admin admin = Admin.builder()
                    .name(registerRequestUserName)
                    .user(user)
                    .roleType(NORMAL_ADMIN)
                    .build();

            adminRepository.save(admin);
            log.info("Registration completed successfully");
            return true;

        } catch (Exception e) {
            log.error("An error occurred while registering admin at the database: " + e.getMessage());
            return false;
        }
    }

    // 토큰으로 관리자 찾기
    @Override
    public Admin findAdminByUserToken(String userToken) {
        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        Optional<Admin> maybeAdmin = adminRepository.findByUser(user);
        if (maybeAdmin.isEmpty()) {
            log.warn("Can not find Admin");
            return null;
        }

        Admin admin = maybeAdmin.get();
        return admin;
    }
}

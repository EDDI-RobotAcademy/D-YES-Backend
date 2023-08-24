package com.dyes.backend.domain.admin.service;

import com.dyes.backend.domain.admin.controller.form.AdminRegisterRequestForm;
import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.request.AdminRegisterRequest;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
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

    @Override
    public boolean adminRegister(AdminRegisterRequestForm registerForm) {

        final String mainAdminUserToken = registerForm.getUserToken();

        if(!mainAdminUserToken.contains("mainadmin")) {
            log.info("Registration Denied: Main Admin Access Only");
            return false;
        }

        final AdminRegisterRequest registerRequest = registerForm.toAdminRegisterRequest();

        final String registerRequestUserId = registerRequest.getId();
        Optional<User> maybeUser = userRepository.findByStringId(registerRequestUserId);

        if(maybeUser.isEmpty()) {
            log.info("Cannot find User");
            return false;
        }
        final User user = maybeUser.get();
        final String registerRequestUserName = registerRequest.getName();

        final Admin admin = Admin.builder()
                .name(registerRequestUserName)
                .user(user)
                .roleType(NORMAL_ADMIN)
                .build();

        adminRepository.save(admin);

        return true;
    }
}

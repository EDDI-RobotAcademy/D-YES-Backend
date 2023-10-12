package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserManagement;
import com.dyes.backend.domain.user.repository.UserManagementRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.response.UserInfoResponseForAdmin;
import com.dyes.backend.domain.user.service.response.UserManagementInfoResponseForAdmin;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseFormForDashBoardForAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementAdminServiceImpl implements UserManagementAdminService{
    final private UserRepository userRepository;
    final private UserManagementRepository userManagementRepository;
    final private AdminRepository adminRepository;
    final private AdminService adminService;

    // 관리자의 회원 목록 조회
    @Override
    public List<UserInfoResponseForm> getUserList(String userToken) {
        log.info("getUserList start");

        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.warn("Can not find admin: user token - {}", userToken);
            return null;
        }

        List<UserInfoResponseForm> userInfoResponseFormList = new ArrayList<>();
        List<User> userList = userRepository.findAll();

        for (User user : userList) {
            UserManagement userManagement = userManagementRepository.findByUser(user);
            Optional<Admin> maybeAdmin = adminRepository.findByUser(user);

            UserInfoResponseForm userInfoResponseForm;

            if (maybeAdmin.isPresent()) {
                Admin isAdmin = maybeAdmin.get();
                userInfoResponseForm
                        = new UserInfoResponseForm(
                        user.getId(), user.getUserType(), user.getActive(),
                        isAdmin.getRoleType(), userManagement.getRegistrationDate());
            } else {
                userInfoResponseForm
                        = new UserInfoResponseForm(
                        user.getId(), user.getUserType(), user.getActive(),
                        userManagement.getRegistrationDate());
            }
            userInfoResponseFormList.add(userInfoResponseForm);
        }

        log.info("getUserList end");
        return userInfoResponseFormList;
    }

    // 관리자의 신규 회원 목록 조회(7일)
    @Override
    public UserInfoResponseFormForDashBoardForAdmin getNewUserList() {
        log.info("getNewUserList start");

        List<UserManagementInfoResponseForAdmin> registeredUserCountList = new ArrayList<>();
        List<UserInfoResponseForAdmin> userInfoResponseForAdminList = new ArrayList<>();

        // 이전 7일간의 내역을 조회
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        List<UserManagement> userManagementList
                = userManagementRepository.findAllByRegistrationDateAfterOrderByRegistrationDateDesc(sevenDaysAgo);

        if (userManagementList.size() == 0) {
            log.info("No users found.");
            return null;
        }

        int[] registeredUserCount = new int[7];

        for (UserManagement userManagement : userManagementList) {
            User user = userManagement.getUser();
            int daysAgo = (int) ChronoUnit.DAYS.between(userManagement.getRegistrationDate(), today);

            if (daysAgo >= 0 && daysAgo < 7) {
                registeredUserCount[daysAgo]++;
            }

            UserInfoResponseForAdmin userInfoResponseForm = new UserInfoResponseForAdmin(
                    user.getId(), user.getUserType(), user.getActive(), userManagement.getRegistrationDate());
            userInfoResponseForAdminList.add(userInfoResponseForm);
        }

        LocalDate date = today;
        for (int i = 0; i < 7; i++) {
            UserManagementInfoResponseForAdmin userManagementInfoResponseForAdmin
                    = new UserManagementInfoResponseForAdmin(date, registeredUserCount[i]);
            registeredUserCountList.add(userManagementInfoResponseForAdmin);
            date = date.minusDays(1);
        }

        UserInfoResponseFormForDashBoardForAdmin userInfoResponseFormForDashBoard
                = new UserInfoResponseFormForDashBoardForAdmin(userInfoResponseForAdminList, registeredUserCountList);

        log.info("getNewUserList end");
        return userInfoResponseFormForDashBoard;
    }
}

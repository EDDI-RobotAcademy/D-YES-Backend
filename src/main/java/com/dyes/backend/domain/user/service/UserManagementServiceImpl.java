package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.authentication.service.kakao.KakaoAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.NaverAuthenticationService;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserManagement;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.entity.UserType;
import com.dyes.backend.domain.user.repository.UserManagementRepository;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.dyes.backend.utility.provider.KakaoOauthSecretsProvider;
import com.dyes.backend.utility.provider.NaverOauthSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.dyes.backend.domain.admin.entity.RoleType.MAIN_ADMIN;
import static com.dyes.backend.domain.admin.entity.RoleType.NORMAL_ADMIN;
import static com.dyes.backend.domain.user.entity.Active.NO;
import static com.dyes.backend.domain.user.entity.Active.YES;
import static com.dyes.backend.domain.user.entity.UserType.*;
import static com.dyes.backend.utility.nickName.NickNameUtils.getRandomNickName;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService{
    final private GoogleOauthSecretsProvider googleOauthSecretsProvider;
    final private NaverOauthSecretsProvider naverOauthSecretsProvider;
    final private KakaoOauthSecretsProvider kakaoOauthSecretsProvider;
    final private GoogleAuthenticationService googleAuthenticationService;
    final private NaverAuthenticationService naverAuthenticationService;
    final private KakaoAuthenticationService kakaoAuthenticationService;
    final private AuthenticationService authenticationService;
    final private UserRepository userRepository;
    final private UserProfileRepository userProfileRepository;
    final private UserManagementRepository userManagementRepository;
    final private AdminRepository adminRepository;
    final private RedisService redisService;

    // 닉네임 중복 확인
    @Override
    public Boolean checkNickNameDuplicate(String nickName) {
        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByNickName(nickName);

        if (maybeUserProfile.isPresent()) {
            log.info("nickname already exists");
            return false;
        }

        return true;
    }

    // 이메일 중복 확인
    @Override
    public Boolean checkEmailDuplicate(String email) {
        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByEmail(email);

        if (maybeUserProfile.isPresent()) {
            log.info("email already exists");
            return false;
        }

        return true;
    }

    // Google 사용자의 TTMARKET 회원가입
    @Override
    public RedirectView userRegisterAndLoginForGoogle(GoogleUserLoginRequestForm requestForm) {
        log.info("userRegisterAndLoginForGoogle start");
        Optional<User> maybeUser = userRepository.findByStringId(requestForm.getId());

        RedirectView redirectView;
        final String platformType = "google";

        if (maybeUser.isEmpty()) {
            redirectView = handleNewUser(
                    requestForm.getId(), requestForm.getAccessToken(),
                    requestForm.getRefreshToken(), null, requestForm.getEmail(),
                    requestForm.getPicture(), null, platformType);
        } else if (maybeUser.get().getActive() == NO) {
            redirectView = handleInactiveUser(
                    requestForm.getAccessToken(), requestForm.getRefreshToken(),
                    null, requestForm.getId(), requestForm.getEmail(),
                    requestForm.getPicture(), null, maybeUser.get(), platformType);
        } else {
            redirectView = handleActiveUser(
                    requestForm.getAccessToken(), requestForm.getRefreshToken(),
                    maybeUser.get(), platformType);
        }

        log.info("userRegisterAndLoginForGoogle end");
        return redirectView;
    }

    // Naver 사용자의 TTMARKET 회원가입
    @Override
    public RedirectView userRegisterAndLoginForNaver(NaverUserLoginRequestForm requestForm) {
        log.info("userRegisterAndLoginForNaver start");

        Optional<User> maybeUser = userRepository.findByStringId(requestForm.getId());

        RedirectView redirectView;
        final String platformType = "naver";

        if (maybeUser.isEmpty()) {
            redirectView = handleNewUser(
                    requestForm.getId(), requestForm.getAccessToken(),
                    requestForm.getRefreshToken(), null, requestForm.getEmail(),
                    requestForm.getProfile_image(), requestForm.getMobile_e164(), platformType);
        } else if (maybeUser.get().getActive() == NO) {
            redirectView = handleInactiveUser(
                    requestForm.getAccessToken(), requestForm.getRefreshToken(),
                    null, requestForm.getId(), requestForm.getEmail(),
                    requestForm.getProfile_image(), requestForm.getMobile_e164(), maybeUser.get(), platformType);
        } else {
            redirectView = handleActiveUser(
                    requestForm.getAccessToken(), requestForm.getRefreshToken(),
                    maybeUser.get(), platformType);
        }

        log.info("userRegisterAndLoginForGoogle end");
        return redirectView;
    }

    // Kakao 사용자의 TTMARKET 회원가입
    @Override
    public RedirectView userRegisterAndLoginForKakao(KakaoUserLoginRequestForm requestForm) {
        log.info("userRegisterAndLoginForKakao start");

        Optional<User> maybeUser = userRepository.findByStringId(requestForm.getId());

        RedirectView redirectView;
        final String platformType = "kakao";

        if (maybeUser.isEmpty()) {
            redirectView = handleNewUser(
                    requestForm.getId(), requestForm.getAccessToken(), requestForm.getRefreshToken(),
                    requestForm.getNickName(), null, requestForm.getPicture(),
                   null, platformType);
        } else if (maybeUser.get().getActive().equals(NO)) {
            redirectView = handleInactiveUser(
                    requestForm.getAccessToken(), requestForm.getRefreshToken(),
                    requestForm.getNickName(), requestForm.getId(), null,
                    requestForm.getPicture(), null, maybeUser.get(), platformType);
        } else {
            redirectView = handleActiveUser(
                    requestForm.getAccessToken(), requestForm.getRefreshToken(),
                    maybeUser.get(), platformType);
        }

        log.info("userRegisterAndLoginForKakao end");
        return redirectView;
    }

    // TTMARKET 로그인
    @Override
    public String userLogIn(User user, String platform) {
        log.info("userLogIn start");

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);

        String profileImg = "";
        String nickName = "";

        try {
            if(maybeUserProfile.isPresent()) {
                profileImg = maybeUserProfile.get().getProfileImg();
                nickName = maybeUserProfile.get().getNickName();

                if (profileImg != null) {
                    profileImg = URLEncoder.encode(profileImg, "UTF-8");
                }

                if (nickName != null) {
                    nickName = URLEncoder.encode(nickName, "UTF-8");
                }
            }

            String userToken = platform + UUID.randomUUID();

            // 관리자의 경우 token에 관리자 여부 추가
            Optional<Admin> maybeAdmin = adminRepository.findByUser(user);
            if (maybeAdmin.isPresent()) {
                Admin admin = maybeAdmin.get();

                if (admin.getRoleType().equals(MAIN_ADMIN)) {
                    userToken = "mainadmin" + userToken;
                } else if (admin.getRoleType().equals(NORMAL_ADMIN)) {
                    userToken = "normaladmin" + userToken;
                }
            }

            redisService.setUserTokenAndUser(userToken, user.getAccessToken());

            // 로그인 후 헤더에 프로필 사진, 닉네임을 띄우기 위해 url에 담아서 전달
            String mainPageUserInfo = userToken + "&profileImg=" + profileImg + "&nickName=" + nickName;

            log.info("userLogIn end");
            return mainPageUserInfo;

        } catch (Exception e) {
            log.error("Failed to login for user {}: {}", user.getId(), e.getMessage(), e);
            return null;
        }
    }

    // TTMARKET 로그아웃
    @Override
    public boolean userLogOut(String userToken) {
        log.info("userLogOut start");

        try {
            log.info("userLogOut end");
            return logOutWithDeleteKeyAndValueInRedis(userToken);

        } catch (Exception e) {
            log.error("Failed to logout {}", e.getMessage(), e);
            return false;
        }
    }

    // 회원 탈퇴(Oauth 연결 끊기 및 DB 삭제)
    @Override
    public boolean userWithdrawal(String userToken) {
        log.info("userWithdrawal start");

        User user = authenticationService.findUserByUserToken(userToken);
        Optional<Admin> maybeAdmin = adminRepository.findByUser(user);

        if (maybeAdmin.isPresent()) {
            log.info("Admin can not withdrawal");
            return false;
        }

        final UserType userType = user.getUserType();
        User withdrawlUser;

        switch (userType) {
            case GOOGLE -> withdrawlUser = googleAuthenticationService.disconnectGoogleUser(user);
            case NAVER -> withdrawlUser = naverAuthenticationService.disconnectNaverUser(user);
            case KAKAO -> withdrawlUser = kakaoAuthenticationService.disconnectKakaoUser(user);
            default -> {
                log.error("Invalid user type for withdrawal");
                return false;
            }
        }

        if (withdrawlUser == null) {
            return false;
        }

        boolean isCompleteDeleteUser = inactiveUser(withdrawlUser);
        if (!isCompleteDeleteUser) {
            return false;
        }

        log.info("userWithdrawal end");
        return userLogOut(userToken);
    }

    // 회원 비활성화 및 프로필 삭제
    public boolean inactiveUser(User user) {
        log.info("inactiveUser start");
        try {
            user.updateActive(NO);
            userRepository.save(user);

            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);

            UserProfile userProfile = new UserProfile();
            if(maybeUserProfile.isPresent()) {
                userProfile = maybeUserProfile.get();
            }

            userProfileRepository.delete(userProfile);

            log.info("inactiveUser end");
            return true;
        } catch (Exception e) {
            log.error("Can't inactive user {}", e.getMessage(), e);
            return false;
        }
    }

    // 로그아웃 요청한 사용자의 userToken을 Redis에서 삭제
    public Boolean logOutWithDeleteKeyAndValueInRedis(String userToken) {
        log.info("logOutWithDeleteKeyAndValueInRedis start");

        try {
            redisService.deleteKeyAndValueWithUserToken(userToken);

            log.info("logOutWithDeleteKeyAndValueInRedis end");
            return true;
        } catch (RedisException e) {
            log.error("Can't not logout with this userToken: {}", userToken, e);
            return false;
        }
    }

    // 활동 상태의 사용자 로그인
    private RedirectView handleActiveUser(String accessToken, String refreshToken, User user, String platformType) {
        user.updateAccessToken(accessToken);

        if(platformType.equals("kakao")) {
            user.updateRefreshToken(refreshToken);
        }

        userRepository.save(user);

        log.info("User is active. Login completed.");
        return getRedirectView(user, platformType);
    }

    // 활동 정지 상태의 사용자 로그인
    private RedirectView handleInactiveUser(
            String accessToken, String refreshToken, String nickName, String id,
            String email, String profileImg, String contactNumber, User user, String platformType) {
        user.updateAllTokenWithActive(accessToken, refreshToken, YES);
        userRepository.save(user);
        saveUserProfile(id, nickName, email, profileImg, contactNumber, user);

        log.info("User is inactive. Activation completed.");
        return getRedirectView(user, platformType);
    }

    // 신규 사용자 로그인
    private RedirectView handleNewUser(
            String id, String accessToken, String refreshToken, String nickName,
            String email, String profileImg, String contactNumber, String platformType) {
        UserType userType = null;

        switch (platformType) {
            case "google" -> userType = GOOGLE;
            case "naver" -> userType = NAVER;
            case "kakao" -> userType = KAKAO;
            default -> log.error("Invalid platform type");
        }

        User user = signUpUser(id, accessToken, refreshToken, userType);
        saveUserProfile(id, nickName, email, profileImg, contactNumber, user);
        saveUserManagement(user);

        log.info("User not registered with us. Sign up completed.");
        return getRedirectView(user, platformType);
    }

    // 사용자 관리 정보 저장
    private void saveUserManagement(User user) {
        UserManagement userManagement = UserManagement.builder()
                .id(user.getId())
                .registrationDate(LocalDate.now())
                .user(user)
                .build();
        userManagementRepository.save(userManagement);
    }

    // 사용자 프로필 정보 저장
    private void saveUserProfile(String id, String nickName, String email, String profileImg, String contactNumber, User user) {
        UserType userType = user.getUserType();
        UserProfile userProfile = new UserProfile();
        if(nickName == null) {
            nickName = getRandomNickName();
        }

        switch (userType) {
            case GOOGLE -> userProfile = UserProfile.builder()
                    .id(id)
                    .nickName(nickName)
                    .email(email)
                    .profileImg(profileImg)
                    .user(user)
                    .build();
            case NAVER -> userProfile = UserProfile.builder()
                    .id(id)
                    .nickName(nickName)
                    .contactNumber(contactNumber)
                    .email(email)
                    .profileImg(profileImg)
                    .user(user)
                    .build();
            case KAKAO -> userProfile = UserProfile.builder()
                    .id(id)
                    .nickName(nickName)
                    .profileImg(profileImg)
                    .user(user)
                    .build();
            default -> log.error("Invalid platform type");
        }

        userProfileRepository.save(userProfile);
    }

    // 사용자 기본 정보 저장
    private User signUpUser(String id, String accessToken, String refreshToken, UserType userType) {
        User user = User.builder()
                .id(id)
                .active(YES)
                .userType(userType)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        userRepository.save(user);
        return user;
    }

    // 사용자 로그인 처리 후 프론트로 Redirect
    private RedirectView getRedirectView(User user, String platformType) {
        String redirectUrl = null;

        switch (platformType) {
            case "google" -> redirectUrl = googleOauthSecretsProvider.getGOOGLE_REDIRECT_VIEW_URL();
            case "naver" -> redirectUrl = naverOauthSecretsProvider.getNAVER_REDIRECT_VIEW_URL();
            case "kakao" -> redirectUrl = kakaoOauthSecretsProvider.getKAKAO_REDIRECT_VIEW_URL();
            default -> log.error("Invalid user type");
        }

        String mainPageUserInfo = userLogIn(user, platformType);
        return new RedirectView(redirectUrl + mainPageUserInfo);
    }
}

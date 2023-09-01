package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.entity.RoleType;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.authentication.service.kakao.service.KakaoAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.NaverAuthenticationService;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.UserProfileModifyRequestForm;
import com.dyes.backend.domain.user.entity.*;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.response.*;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.dyes.backend.utility.provider.KakaoOauthSecretsProvider;
import com.dyes.backend.utility.provider.NaverOauthSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

import static com.dyes.backend.utility.nickName.NickNameUtils.getRandomNickName;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final private GoogleOauthSecretsProvider googleOauthSecretsProvider;
    final private NaverOauthSecretsProvider naverOauthSecretsProvider;
    final private KakaoOauthSecretsProvider kakaoOauthSecretsProvider;
    final private GoogleAuthenticationService googleAuthenticationService;
    final private NaverAuthenticationService naverAuthenticationService;
    final private KakaoAuthenticationService kakaoAuthenticationService;
    final private AuthenticationService authenticationService;
    final private UserRepository userRepository;
    final private UserProfileRepository userProfileRepository;
    final private AdminRepository adminRepository;
    final private RedisService redisService;

    // 닉네임 중복 확인
    @Override
    public Boolean checkNickNameDuplicate(String nickName) {
        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByNickName(nickName);

        if(maybeUserProfile.isPresent()) {
            log.info("nickname already exists");
            return false;
        }

        return true;
    }

    // 이메일 중복 확인
    @Override
    public Boolean checkEmailDuplicate(String email) {
        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByEmail(email);

        if(maybeUserProfile.isPresent()) {
            log.info("email already exists");
            return false;
        }

        return true;
    }

    // 프로필 확인
    @Override
    public UserProfileResponseForm getUserProfile(String userToken) {
        final User user = authenticationService.findUserByUserToken(userToken);
        if(user == null) {
            return null;
        }

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);

        if(maybeUserProfile.isEmpty()) {
            UserProfileResponseForm userProfileResponseForm = new UserProfileResponseForm(user.getId());
            return userProfileResponseForm;
        }

        UserProfile userProfile = maybeUserProfile.get();
        UserProfileResponseForm userProfileResponseForm
                = new UserProfileResponseForm(
                    user.getId(),
                    userProfile.getNickName(),
                    userProfile.getEmail(),
                    userProfile.getProfileImg(),
                    userProfile.getContactNumber(),
                    userProfile.getAddress());

        return userProfileResponseForm;
    }

    // 프로필 수정
    @Override
    public UserProfileResponseForm modifyUserProfile(UserProfileModifyRequestForm requestForm) {
        final User user = authenticationService.findUserByUserToken(requestForm.getUserToken());
        if(user == null) {
            return null;
        }

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
        if(maybeUserProfile.isEmpty()) {

            Address address = new Address(requestForm.getAddress(), requestForm.getZipCode(), requestForm.getAddressDetail());

            UserProfile userProfile = UserProfile.builder()
                    .id(user.getId())
                    .nickName(requestForm.getNickName())
                    .email(requestForm.getEmail())
                    .profileImg(requestForm.getProfileImg())
                    .contactNumber(requestForm.getContactNumber())
                    .address(address)
                    .user(user)
                    .build();

            userProfileRepository.save(userProfile);

            UserProfileResponseForm userProfileResponseForm
                    = new UserProfileResponseForm(
                        user.getId(),
                        userProfile.getNickName(),
                        userProfile.getEmail(),
                        userProfile.getProfileImg(),
                        userProfile.getContactNumber(),
                        userProfile.getAddress());

            return userProfileResponseForm;
        }

        Address address = new Address(requestForm.getAddress(), requestForm.getZipCode(), requestForm.getAddressDetail());
        UserProfile userProfile = maybeUserProfile.get();

        userProfile.setNickName(requestForm.getNickName());
        userProfile.setEmail(requestForm.getEmail());
        userProfile.setProfileImg(requestForm.getProfileImg());
        userProfile.setContactNumber(requestForm.getContactNumber());
        userProfile.setAddress(address);

        userProfileRepository.save(userProfile);

        UserProfileResponseForm userProfileResponseForm
                = new UserProfileResponseForm(
                    user.getId(),
                    userProfile.getNickName(),
                    userProfile.getEmail(),
                    userProfile.getProfileImg(),
                    userProfile.getContactNumber(),
                    userProfile.getAddress());

        return userProfileResponseForm;
    }

    // Google 사용자의 TTMARKET 회원가입
    @Override
    public RedirectView userRegisterAndLoginForGoogle(GoogleUserLoginRequestForm requestForm) {
        log.info("userLogInForGoogle start");

        Optional<User> maybeUser = userRepository.findByStringId(requestForm.getId());
        if (maybeUser.isEmpty()) {
            User user = User.builder()
                    .id(requestForm.getId())
                    .active(Active.YES)
                    .userType(UserType.GOOGLE)
                    .accessToken(requestForm.getAccessToken())
                    .refreshToken(requestForm.getRefreshToken())
                    .build();
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(requestForm.getId())
                    .nickName(getRandomNickName())
                    .email(requestForm.getEmail())
                    .profileImg(requestForm.getPicture())
                    .build();
            userProfileRepository.save(userProfile);
            log.info("userLogInForGoogle Not Our User");
            log.info("userLogInForGoogle end");

            String redirectUrl = googleOauthSecretsProvider.getGOOGLE_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "google");
            return new RedirectView(redirectUrl + mainPageUserInfo);

        } else if (maybeUser.get().getActive() == Active.NO) {
            User user = maybeUser.get();
            user.setActive(Active.YES);
            user.setAccessToken(requestForm.getAccessToken());
            user.setRefreshToken(requestForm.getRefreshToken());
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(requestForm.getId())
                    .nickName(getRandomNickName())
                    .email(requestForm.getEmail())
                    .profileImg(requestForm.getPicture())
                    .build();
            userProfileRepository.save(userProfile);
            log.info("userLogInForGoogle rejoin user");
            log.info("userLogInForGoogle end");

            String redirectUrl = googleOauthSecretsProvider.getGOOGLE_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "google");
            return new RedirectView(redirectUrl + mainPageUserInfo);
        } else {
            log.info("userLogInForGoogle OurUser");
            User user = maybeUser.get();
            user.setAccessToken(requestForm.getAccessToken());
            userRepository.save(user);
            log.info("userLogInForGoogle end");

            String redirectUrl = googleOauthSecretsProvider.getGOOGLE_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "google");
            return new RedirectView(redirectUrl + mainPageUserInfo);
        }
    }

    // Naver 사용자의 TTMARKET 회원가입
    @Override
    public RedirectView userRegisterAndLoginForNaver(NaverUserLoginRequestForm requestForm) {
        log.info("userRegisterAndLoginForNaver start");

        Optional<User> maybeUser = userRepository.findByStringId(requestForm.getId());
        if (maybeUser.isEmpty()) {
            User user = User.builder()
                    .id(requestForm.getId())
                    .active(Active.YES)
                    .userType(UserType.NAVER)
                    .accessToken(requestForm.getAccessToken())
                    .refreshToken(requestForm.getRefreshToken())
                    .build();
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(requestForm.getId())
                    .nickName(getRandomNickName())
                    .contactNumber(requestForm.getMobile_e164())
                    .email(requestForm.getEmail())
                    .profileImg(requestForm.getProfile_image())
                    .build();
            userProfileRepository.save(userProfile);
            log.info("userRegisterAndLoginForNaver Not Our User");
            log.info("userRegisterAndLoginForNaver end");

            String redirectUrl = naverOauthSecretsProvider.getNAVER_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "naver");
            return new RedirectView(redirectUrl + mainPageUserInfo);

        } else if (maybeUser.get().getActive() == Active.NO) {
            User user = maybeUser.get();
            user.setActive(Active.YES);
            user.setAccessToken(requestForm.getAccessToken());
            user.setRefreshToken(requestForm.getRefreshToken());
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(requestForm.getId())
                    .nickName(getRandomNickName())
                    .contactNumber(requestForm.getMobile_e164())
                    .email(requestForm.getEmail())
                    .profileImg(requestForm.getProfile_image())
                    .build();
            userProfileRepository.save(userProfile);
            log.info("userRegisterAndLoginForNaver rejoin user");
            log.info("userRegisterAndLoginForNaver end");

            String redirectUrl = naverOauthSecretsProvider.getNAVER_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "naver");
            return new RedirectView(redirectUrl + mainPageUserInfo);
        } else {
            log.info("userRegisterAndLoginForNaver OurUser");
            User user = maybeUser.get();
            user.setAccessToken(requestForm.getAccessToken());
            userRepository.save(user);
            log.info("userRegisterAndLoginForNaver end");

            String redirectUrl = naverOauthSecretsProvider.getNAVER_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "naver");
            return new RedirectView(redirectUrl + mainPageUserInfo);
        }
    }

    // Kakao 사용자의 TTMARKET 회원가입
    @Override
    public RedirectView userRegisterAndLoginForKakao(KakaoUserLoginRequestForm requestForm) {
        Optional<User> maybeUser = userRepository.findByStringId(requestForm.getId());

        // 없다면 회원가입(사용자, 사용자 프로필 생성)
        if(maybeUser.isEmpty()) {
            User user = User.builder()
                    .id(requestForm.getId())
                    .active(Active.YES)
                    .userType(UserType.KAKAO)
                    .accessToken(requestForm.getAccessToken())
                    .refreshToken(requestForm.getRefreshToken())
                    .build();

            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(requestForm.getId())
                    .nickName(requestForm.getNickName())
                    .profileImg(requestForm.getPicture())
                    .build();

            userProfileRepository.save(userProfile);

            String redirectUrl = kakaoOauthSecretsProvider.getKAKAO_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "kakao");
            return new RedirectView(redirectUrl + mainPageUserInfo);

        } else if(maybeUser.isPresent() && maybeUser.get().getActive().equals(Active.YES)) {

            // 활동하고 있는 회원이면 accessToken, refreshToken 갱신 후 로그인
            final User user = maybeUser.get();
            user.setAccessToken(requestForm.getAccessToken());
            user.setRefreshToken(requestForm.getRefreshToken());
            userRepository.save(user);

            String redirectUrl = kakaoOauthSecretsProvider.getKAKAO_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "kakao");
            return new RedirectView(redirectUrl + mainPageUserInfo);

        } else if(maybeUser.isPresent() && maybeUser.get().getActive().equals(Active.NO)) {

            // 탈퇴한 회원이면 Active YES로 변경 후 프로필 재생성
            final User user = maybeUser.get();
            user.setActive(Active.YES);
            user.setAccessToken(requestForm.getAccessToken());
            user.setRefreshToken(requestForm.getRefreshToken());
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(requestForm.getId())
                    .nickName(requestForm.getNickName())
                    .profileImg(requestForm.getPicture())
                    .build();

            userProfileRepository.save(userProfile);

            String redirectUrl = kakaoOauthSecretsProvider.getKAKAO_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "kakao");
            return new RedirectView(redirectUrl + mainPageUserInfo);
        }

        return null;
    }

    // TTMARKET 로그인
    @Override
    public String userLogIn(User user, String platform) {
        Optional<UserProfile> userProfile = userProfileRepository.findByUser(user);

        try {
            String encodedProfileImg = URLEncoder.encode(userProfile.get().getProfileImg(), "UTF-8");
            String encodedNickName = URLEncoder.encode(userProfile.get().getNickName(), "UTF-8");

            String userToken = platform + UUID.randomUUID();

            Optional<Admin> maybeAdmin = adminRepository.findByUser(user);
            if(maybeAdmin.isPresent()) {
                Admin admin = maybeAdmin.get();

                if(admin.getRoleType().equals(RoleType.MAIN_ADMIN)) {
                    userToken = "mainadmin" + userToken;
                } else if (admin.getRoleType().equals(RoleType.NORMAL_ADMIN)) {
                    userToken = "normaladmin" + userToken;
                }
            }

            redisService.setUserTokenAndUser(userToken, user.getAccessToken());

            // 로그인 후 헤더에 프로필 사진, 닉네임을 띄우기 위해 url에 담아서 전달
            String mainPageUserInfo = userToken + "&profileImg=" + encodedProfileImg + "&nickName=" + encodedNickName;
            return mainPageUserInfo;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // 로그아웃
    @Override
    public boolean userLogOut(String userToken) {
        log.info("userLogOut start");
        try {
            logOutWithDeleteKeyAndValueInRedis(userToken);
            return true;
        } catch (Exception e) {
            log.error("Can't logOut {}", e.getMessage(), e);
            return false;
        }
    }

    // 회원 탈퇴(Oauth 연결 끊기 및 DB 삭제)
    @Override
    public boolean userWithdrawal(String userToken) {
        User user = authenticationService.findUserByUserToken(userToken);
        Optional<Admin> maybeAdmin = adminRepository.findByUser(user);

        if(maybeAdmin.isPresent()) {
            log.info("Admin can not withdrawal");
            return false;
        }

        final UserType userType = user.getUserType();

        if (userType.equals(UserType.GOOGLE)) {
            User withdrawlUser = googleAuthenticationService.googleUserDisconnect(user);
            if(withdrawlUser == null) {
                return false;
            }
            Boolean isCompleteDeleteUser = inactiveUser(withdrawlUser);
            if(isCompleteDeleteUser == false) {
                return false;
            }
            return userLogOut(userToken);

        } else if (userType.equals(UserType.NAVER)) {
            User withdrawlUser = naverAuthenticationService.naverUserDisconnect(user);
            if(withdrawlUser == null) {
                return false;
            }
            Boolean isCompleteDeleteUser = inactiveUser(withdrawlUser);
            if(isCompleteDeleteUser == false) {
                return false;
            }
            return userLogOut(userToken);

        } else if (userType.equals(UserType.KAKAO)){
            User withdrawlUser = kakaoAuthenticationService.kakaoUserDisconnect(user);
            if(withdrawlUser == null) {
                return false;
            }
            Boolean isCompleteDeleteUser = inactiveUser(withdrawlUser);
            if(isCompleteDeleteUser == false) {
                return false;
            }
            return userLogOut(userToken);
        } else {
            return false;
        }
    }

    // 회원 비활성화 및 프로필 삭제
    public boolean inactiveUser(User user) {
        log.info("inactiveUser start");
        try {
            user.setActive(Active.NO);
            userRepository.save(user);

            UserProfile userProfile = userProfileRepository.findByUser(user).get();
            userProfileRepository.delete(userProfile);

            return true;
        } catch (Exception e) {
            log.error("Can't inactive user {}", e.getMessage(), e);
            return false;
        }
    }

    // 사용자의 Oauth 채널 판별
    public String divideUserByPlatform(String userToken) {
        log.info("divideUserByPlatform start");
        String platform;
        if (userToken.contains("google")){
            platform = "google";
            log.info("divideUserByPlatform end");
            return platform;
        } else if (userToken.contains("naver")) {
            platform = "naver";
            log.info("divideUserByPlatform end");
            return platform;
        } else {
            platform = "kakao";
            log.info("divideUserByPlatform end");
            return platform;
        }
    }

    // 로그아웃 요청한 사용자의 userToken
    public Boolean logOutWithDeleteKeyAndValueInRedis (String userToken) {
        log.info("logOutWithDeleteKeyAndValueInRedis start");
        try {
            redisService.deleteKeyAndValueWithUserToken(userToken);
            log.info("logOutWithDeleteKeyAndValueInRedis end");
            return true;
        } catch (RedisException e) {
            log.error("Can't not logout with this userToken: {}", userToken, e);
            log.info("logOutWithDeleteKeyAndValueInRedis end");
            return false;
        }
    }

}

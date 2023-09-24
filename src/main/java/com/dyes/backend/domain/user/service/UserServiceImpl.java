package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.entity.RoleType;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.authentication.service.kakao.service.KakaoAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.NaverAuthenticationService;
import com.dyes.backend.domain.user.controller.form.*;
import com.dyes.backend.domain.user.entity.*;
import com.dyes.backend.domain.user.repository.AddressBookRepository;
import com.dyes.backend.domain.user.repository.UserManagementRepository;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.request.UserAddressModifyRequest;
import com.dyes.backend.domain.user.service.request.UserAddressOptionChangeRequest;
import com.dyes.backend.domain.user.service.request.UserAddressUpdateRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import com.dyes.backend.domain.user.service.response.UserInfoResponseForAdmin;
import com.dyes.backend.domain.user.service.response.UserManagementInfoResponseForAdmin;
import com.dyes.backend.domain.user.service.response.form.UserAddressBookResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseFormForDashBoardForAdmin;
import com.dyes.backend.domain.user.service.response.form.UserProfileResponseForm;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dyes.backend.domain.user.entity.AddressBookOption.DEFAULT_OPTION;
import static com.dyes.backend.domain.user.entity.AddressBookOption.NON_DEFAULT_OPTION;
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
    final private UserManagementRepository userManagementRepository;
    final private AddressBookRepository addressBookRepository;
    final private AdminRepository adminRepository;
    final private RedisService redisService;
    final private AdminService adminService;

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

    // 프로필 확인
    @Override
    public UserProfileResponseForm getUserProfile(String userToken) {
        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);

        if (maybeUserProfile.isEmpty()) {
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
        if (user == null) {
            return null;
        }

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
        if (maybeUserProfile.isEmpty()) {

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
        if (maybeUser.isEmpty()) {
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

        } else if (maybeUser.isPresent() && maybeUser.get().getActive().equals(Active.YES)) {

            // 활동하고 있는 회원이면 accessToken, refreshToken 갱신 후 로그인
            final User user = maybeUser.get();
            user.setAccessToken(requestForm.getAccessToken());
            user.setRefreshToken(requestForm.getRefreshToken());
            userRepository.save(user);

            String redirectUrl = kakaoOauthSecretsProvider.getKAKAO_REDIRECT_VIEW_URL();
            String mainPageUserInfo = userLogIn(user, "kakao");
            return new RedirectView(redirectUrl + mainPageUserInfo);

        } else if (maybeUser.isPresent() && maybeUser.get().getActive().equals(Active.NO)) {

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
            if (maybeAdmin.isPresent()) {
                Admin admin = maybeAdmin.get();

                if (admin.getRoleType().equals(RoleType.MAIN_ADMIN)) {
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

        if (maybeAdmin.isPresent()) {
            log.info("Admin can not withdrawal");
            return false;
        }

        final UserType userType = user.getUserType();

        if (userType.equals(UserType.GOOGLE)) {
            User withdrawlUser = googleAuthenticationService.googleUserDisconnect(user);
            if (withdrawlUser == null) {
                return false;
            }
            Boolean isCompleteDeleteUser = inactiveUser(withdrawlUser);
            if (isCompleteDeleteUser == false) {
                return false;
            }
            return userLogOut(userToken);

        } else if (userType.equals(UserType.NAVER)) {
            User withdrawlUser = naverAuthenticationService.naverUserDisconnect(user);
            if (withdrawlUser == null) {
                return false;
            }
            Boolean isCompleteDeleteUser = inactiveUser(withdrawlUser);
            if (isCompleteDeleteUser == false) {
                return false;
            }
            return userLogOut(userToken);

        } else if (userType.equals(UserType.KAKAO)) {
            User withdrawlUser = kakaoAuthenticationService.kakaoUserDisconnect(user);
            if (withdrawlUser == null) {
                return false;
            }
            Boolean isCompleteDeleteUser = inactiveUser(withdrawlUser);
            if (isCompleteDeleteUser == false) {
                return false;
            }
            return userLogOut(userToken);
        } else {
            return false;
        }
    }

    // 사용자 배송지 정보 업데이트
    @Override
    public Boolean updateAddress(UserAddressModifyRequestForm requestForm) {
        final User user = authenticationService.findUserByUserToken(requestForm.getUserToken());
        if (user == null) {
            return false;
        }

        UserAddressModifyRequest userAddressModifyRequest = requestForm.toUserAddressModifyRequest();
        Address address = userAddressModifyRequest.toAddress();

        try {
            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
            if (maybeUserProfile.isPresent()) {
                UserProfile userProfile = maybeUserProfile.get();
                userProfile.setAddress(address);
                userProfileRepository.save(userProfile);
            }
            return true;

        } catch (Exception e) {
            log.error("Failed to update the user address: {}", e.getMessage(), e);
            return false;
        }
    }

    // 사용자 주소록 조회(배송지 정보)
    @Override
    public List<UserAddressBookResponseForm> getAddressBook(String userToken) {
        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }
        List<UserAddressBookResponseForm> userAddressBookResponseFormList = new ArrayList<>();

        List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);
        if (addressBookList.size() == 0) {
            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
            if (maybeUserProfile.isEmpty()) {
                return null;
            } else if (maybeUserProfile.isPresent()) {
                UserProfile userProfile = maybeUserProfile.get();
                if (userProfile.getAddress().getAddress().equals(null) || userProfile.getAddress().getAddress().equals("")) {
                    log.info("No address is registered.");
                    return null;
                }
                AddressBook addressBook = AddressBook.builder()
                        .addressBookOption(DEFAULT_OPTION)
                        .contactNumber(userProfile.getContactNumber())
                        .address(userProfile.getAddress())
                        .user(user)
                        .build();
                addressBookRepository.save(addressBook);
                UserAddressBookResponseForm userAddressBookResponseForm
                        = new UserAddressBookResponseForm(
                        addressBook.getId(),
                        addressBook.getAddressBookOption(),
                        addressBook.getReceiver(),
                        addressBook.getContactNumber(),
                        addressBook.getAddress());
                userAddressBookResponseFormList.add(userAddressBookResponseForm);

                return userAddressBookResponseFormList;
            }
        } else {
            for (AddressBook addressBook : addressBookList) {
                UserAddressBookResponseForm userAddressBookResponseForm
                        = new UserAddressBookResponseForm(
                        addressBook.getId(),
                        addressBook.getAddressBookOption(),
                        addressBook.getReceiver(),
                        addressBook.getContactNumber(),
                        addressBook.getAddress());
                userAddressBookResponseFormList.add(userAddressBookResponseForm);
            }
            return userAddressBookResponseFormList;
        }
        return null;
    }

    // 사용자 주소록 추가(배송지 정보)
    @Override
    public Boolean updateAddressBook(UserAddressUpdateRequestForm requestForm) {
        final User user = authenticationService.findUserByUserToken(requestForm.getUserToken());
        if (user == null) {
            return null;
        }
        UserAddressUpdateRequest userAddressUpdateRequest = requestForm.toUserAddressUpdateRequest();
        if (userAddressUpdateRequest.getAddress().equals(null)) {
            log.info("The address to be added to the address book is not available");
            return false;
        }
        List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);
        if (addressBookList.size() == 5) {
            log.info("The maximum registration limit is 5. Unable to add to the address book");
            return false;
        }

        try {
            if (userAddressUpdateRequest.getAddressBookOption().equals(DEFAULT_OPTION)) {
                Optional<AddressBook> maybeAddressBook = addressBookRepository.findByAddressBookOption(DEFAULT_OPTION);
                if (maybeAddressBook.isPresent()) {
                    AddressBook addressBook = maybeAddressBook.get();
                    addressBook.setAddressBookOption(NON_DEFAULT_OPTION);
                    addressBookRepository.save(addressBook);
                }
            }
            AddressBook addressBook = AddressBook.builder()
                    .addressBookOption(userAddressUpdateRequest.getAddressBookOption())
                    .receiver(userAddressUpdateRequest.getReceiver())
                    .contactNumber(userAddressUpdateRequest.getContactNumber())
                    .address(userAddressUpdateRequest.getAddress())
                    .user(user)
                    .build();

            addressBookRepository.save(addressBook);

            log.info("Update address book successful");
            return true;

        } catch (Exception e) {
            log.error("Failed to update the address book: {}", e.getMessage(), e);
            return false;
        }
    }

    // 관리자의 회원 목록 조회
    @Override
    public List<UserInfoResponseForm> getUserList(String userToken) {
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Can not find Admin");
            return null;
        }

        List<UserInfoResponseForm> userInfoResponseFormList = new ArrayList<>();
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            Optional<Admin> maybeAdmin = adminRepository.findByUser(user);
            UserInfoResponseForm userInfoResponseForm;

            if (maybeAdmin.isPresent()) {
                Admin isAdmin = maybeAdmin.get();
                userInfoResponseForm
                        = new UserInfoResponseForm(user.getId(), user.getUserType(), user.getActive(), isAdmin.getRoleType());
            } else {
                userInfoResponseForm
                        = new UserInfoResponseForm(user.getId(), user.getUserType(), user.getActive());
            }
            userInfoResponseFormList.add(userInfoResponseForm);
        }
        return userInfoResponseFormList;
    }

    // 사용자 주소록 삭제(배송지 정보)
    @Override
    public Boolean deleteAddressBook(Long addressBookId, AddressBookDeleteRequestForm deleteForm) {
        log.info("Deleting addressBook with ID: {}", addressBookId);

        UserAuthenticationRequest userAuthenticationRequest = deleteForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        // 주소록 삭제 진행
        try {
            List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);
            for (AddressBook addressBook : addressBookList) {
                if (addressBook.getId().equals(addressBookId)) {
                    addressBookRepository.deleteById(addressBook.getId().toString());
                    log.info("AddressBook deletion successful for addressBook with ID: {}", addressBook.getId());
                    return true;
                }
            }
            return true;

        } catch (Exception e) {
            log.error("Failed to delete the addressBook: {}", e.getMessage(), e);
            return false;
        }
    }

    // 사용자 주소록에 있는 배송지 옵션 변경
    @Override
    public Boolean changeAddressBookOption(UserAddressOptionChangeRequestForm requestForm) {
        log.info("Changing addressBook option with ID: {}", requestForm.getAddressBookId());

        UserAuthenticationRequest userAuthenticationRequest = requestForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        UserAddressOptionChangeRequest userAddressOptionChangeRequest = requestForm.toUserAddressOptionChangeRequest();
        Long addressBookId = userAddressOptionChangeRequest.getAddressBookId();
        AddressBookOption addressBookOption = userAddressOptionChangeRequest.getAddressBookOption();

        // 주소록에 있는 배송지 옵션 변경 진행
        try {
            List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);
            for (AddressBook addressBook : addressBookList) {
                if (addressBook.getId().equals(addressBookId)) {
                    if (addressBookOption.equals(DEFAULT_OPTION)) {
                        Optional<AddressBook> maybeAddressBook = addressBookRepository.findByAddressBookOption(DEFAULT_OPTION);
                        if (maybeAddressBook.isPresent()) {
                            AddressBook addressBookDefaultOption = maybeAddressBook.get();
                            addressBookDefaultOption.setAddressBookOption(NON_DEFAULT_OPTION);
                            addressBookRepository.save(addressBookDefaultOption);
                        }
                    }
                    addressBook.setAddressBookOption(addressBookOption);
                    addressBookRepository.save(addressBook);
                    log.info("AddressBook Option change successful for addressBook with ID: {}", addressBook.getId());
                    return true;
                }
            }
            return true;

        } catch (Exception e) {
            log.error("Failed to change the addressBook option: {}", e.getMessage(), e);
            return false;
        }

    }

    // 관리자의 신규 회원 목록 조회(7일)
    @Override
    public UserInfoResponseFormForDashBoardForAdmin getNewUserList() {
        log.info("Finding New registration User start");

        // 최종적으로 반환할 ResponseForm에 들어갈 Response
        List<UserManagementInfoResponseForAdmin> registeredUserCountList = new ArrayList<>();
        List<UserInfoResponseForAdmin> userInfoResponseForAdminList = new ArrayList<>();

        // 이전 7일간의 내역을 조회
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        List<UserManagement> userManagementList
                = userManagementRepository.findAllByRegistrationDateAfterOrderByRegistrationDateDesc(sevenDaysAgo);
        if(userManagementList.size() == 0) {
            log.info("No users found.");
            return null;
        }
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> userCountList = new ArrayList<>();
        int registeredUserCountToday = 0;
        int registeredUserCount1DayAgo = 0;
        int registeredUserCount2DaysAgo = 0;
        int registeredUserCount3DaysAgo = 0;
        int registeredUserCount4DaysAgo = 0;
        int registeredUserCount5DaysAgo = 0;
        int registeredUserCount6DaysAgo = 0;

        for (UserManagement userManagement : userManagementList) {
            User user = userManagement.getUser();

            if (userManagement.getRegistrationDate().equals(today)) {
                registeredUserCountToday = registeredUserCountToday + 1;
            } else if (userManagement.getRegistrationDate().equals(today.minusDays(1))) {
                registeredUserCount1DayAgo = registeredUserCount1DayAgo + 1;
            } else if (userManagement.getRegistrationDate().equals(today.minusDays(2))) {
                registeredUserCount2DaysAgo = registeredUserCount2DaysAgo + 1;
            } else if (userManagement.getRegistrationDate().equals(today.minusDays(3))) {
                registeredUserCount3DaysAgo = registeredUserCount3DaysAgo + 1;
            } else if (userManagement.getRegistrationDate().equals(today.minusDays(4))) {
                registeredUserCount4DaysAgo = registeredUserCount4DaysAgo + 1;
            } else if (userManagement.getRegistrationDate().equals(today.minusDays(5))) {
                registeredUserCount5DaysAgo = registeredUserCount5DaysAgo + 1;
            } else if (userManagement.getRegistrationDate().equals(today.minusDays(6))) {
                registeredUserCount6DaysAgo = registeredUserCount6DaysAgo + 1;
            }

            UserInfoResponseForAdmin userInfoResponseForm
                    = new UserInfoResponseForAdmin(user.getId(), user.getUserType(), user.getActive(), userManagement.getRegistrationDate());
            userInfoResponseForAdminList.add(userInfoResponseForm);

        }
        dateList.add(today);
        dateList.add(today.minusDays(1));
        dateList.add(today.minusDays(2));
        dateList.add(today.minusDays(3));
        dateList.add(today.minusDays(4));
        dateList.add(today.minusDays(5));
        dateList.add(today.minusDays(6));

        userCountList.add(registeredUserCountToday);
        userCountList.add(registeredUserCount1DayAgo);
        userCountList.add(registeredUserCount2DaysAgo);
        userCountList.add(registeredUserCount3DaysAgo);
        userCountList.add(registeredUserCount4DaysAgo);
        userCountList.add(registeredUserCount5DaysAgo);
        userCountList.add(registeredUserCount6DaysAgo);

        for (int i = 0; i < 7; i++) {
            UserManagementInfoResponseForAdmin userManagementInfoResponseForAdmin
                    = new UserManagementInfoResponseForAdmin(dateList.get(i), userCountList.get(i));
            registeredUserCountList.add(userManagementInfoResponseForAdmin);
        }

        UserInfoResponseFormForDashBoardForAdmin userInfoResponseFormForDashBoard
                = new UserInfoResponseFormForDashBoardForAdmin(userInfoResponseForAdminList, registeredUserCountList);

        log.info("Finding New registration User successful");
        return userInfoResponseFormForDashBoard;
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
        if (userToken.contains("google")) {
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
    public Boolean logOutWithDeleteKeyAndValueInRedis(String userToken) {
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

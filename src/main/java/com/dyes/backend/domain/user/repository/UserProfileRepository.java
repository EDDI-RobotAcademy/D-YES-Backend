package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByNickName(String nickname);

    Optional<UserProfile> findByEmail(String email);
}

package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByNickName(String nickname);

    Optional<UserProfile> findByEmail(String email);

    @Query("select up FROM UserProfile up join fetch up.user u where u = :user")
    Optional<UserProfile> findByUser(User user);
}

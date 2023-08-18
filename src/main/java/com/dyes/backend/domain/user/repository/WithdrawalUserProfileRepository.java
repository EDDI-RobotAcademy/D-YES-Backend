package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalUserProfileRepository extends JpaRepository<UserProfile, String> {
}

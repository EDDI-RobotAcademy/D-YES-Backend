package com.dyes.backend.domain.admin.repository;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser(User user);
}

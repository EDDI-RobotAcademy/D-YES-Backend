package com.dyes.backend.domain.admin.repository;

import com.dyes.backend.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}

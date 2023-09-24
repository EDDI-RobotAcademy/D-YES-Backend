package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.UserManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserManagementRepository extends JpaRepository<UserManagement, String> {
    @Query("select um FROM UserManagement um join fetch um.user where um.registrationDate > :startDate ORDER BY um.registrationDate DESC")
    List<UserManagement> findAllByRegistrationDateAfterOrderByRegistrationDateDesc(@Param("startDate") LocalDate startDate);
}

package com.dyes.backend.domain.inquiry.repository;

import com.dyes.backend.domain.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query("SELECT i FROM Inquiry i JOIN FETCH i.user JOIN FETCH i.content WHERE i.id = :inquiryId")
    Optional<Inquiry> findByIdWithUserContent(@Param("inquiryId") Long inquiryId);
}

package com.dyes.backend.domain.inquiry.repository;

import com.dyes.backend.domain.inquiry.entity.InquiryContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryContentRepository extends JpaRepository<InquiryContent, Long> {
}

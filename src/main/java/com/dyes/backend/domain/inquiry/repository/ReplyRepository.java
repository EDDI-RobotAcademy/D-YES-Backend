package com.dyes.backend.domain.inquiry.repository;

import com.dyes.backend.domain.inquiry.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}

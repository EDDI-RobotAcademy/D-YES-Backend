package com.dyes.backend.domain.event.repository;

import com.dyes.backend.domain.event.entity.EventDeadLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDeadLineRepository extends JpaRepository<EventDeadLine, Long> {
}

package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.LogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggerRepository extends JpaRepository<LogRecord, Long> {
}

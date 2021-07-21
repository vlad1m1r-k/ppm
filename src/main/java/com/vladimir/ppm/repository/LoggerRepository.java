package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.LogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface LoggerRepository extends JpaRepository<LogRecord, Long> {
    void deleteAllByDateBefore(Date date);
}

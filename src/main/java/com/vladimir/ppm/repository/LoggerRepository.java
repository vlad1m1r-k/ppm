package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.LogRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface LoggerRepository extends JpaRepository<LogRecord, Long> {
    void deleteAllByDateBefore(Date date);

    @Query("SELECT r FROM LogRecord AS r WHERE r.user LIKE %?1% OR r.act LIKE %?1% OR r.object LIKE %?1% " +
            "OR r.objName LIKE %?1% OR r.date LIKE %?1% OR r.comment LIKE %?1%")
    Page<LogRecord> search(String text, Pageable pageable);
}

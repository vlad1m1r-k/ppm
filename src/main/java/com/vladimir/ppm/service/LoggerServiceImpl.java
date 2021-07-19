package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Acts;
import com.vladimir.ppm.domain.LogRecord;
import com.vladimir.ppm.domain.Objects;
import com.vladimir.ppm.repository.LoggerRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoggerServiceImpl implements LoggerService {
    private final LoggerRepository repository;

    public LoggerServiceImpl(LoggerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void log(String user, Acts act, Objects object, String objName, Date date, String comment) {
        LogRecord record = new LogRecord(user, act, object, objName, date, comment);
        repository.save(record);
    }
}

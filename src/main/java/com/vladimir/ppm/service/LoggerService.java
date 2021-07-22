package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.LogRecord;
import com.vladimir.ppm.domain.Token;
import org.springframework.data.domain.Page;

public interface LoggerService {
    Page<LogRecord> getLogs(Token token, int page, int size, String direction, String field);
    Page<LogRecord> search(Token token, int page, int size, String direction, String field, String text);
}

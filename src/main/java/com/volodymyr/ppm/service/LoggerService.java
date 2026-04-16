package com.volodymyr.ppm.service;

import org.springframework.data.domain.Page;

import com.volodymyr.ppm.domain.LogRecord;
import com.volodymyr.ppm.domain.Token;

public interface LoggerService {
    Page<LogRecord> getLogs(Token token, int page, int size, String direction, String field);
    Page<LogRecord> search(Token token, int page, int size, String direction, String field, String text);
}

package com.volodymyr.ppm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.volodymyr.ppm.domain.LogRecord;
import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.repository.LoggerRepository;

@Service
public class LoggerServiceImpl implements LoggerService {
    private final LoggerRepository repository;
    private final UserService userService;
    private final ValidatorService validatorService;

    public LoggerServiceImpl(LoggerRepository repository, UserService userService, ValidatorService validatorService) {
        this.repository = repository;
        this.userService = userService;
        this.validatorService = validatorService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LogRecord> getLogs(Token token, int page, int size, String direction, String field) {
        if (userService.isAdmin(token) && validatorService.validateString(direction) && validatorService.validateString(field)) {
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);
            return repository.findAll(pageable);
        }
        return Page.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LogRecord> search(Token token, int page, int size, String direction, String field, String text) {
        if (userService.isAdmin(token) && validatorService.validateString(direction) && validatorService.validateString(field)
                && validatorService.validateString(text)) {
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);
            return repository.search(text, pageable);
        }
        return Page.empty();
    }
}

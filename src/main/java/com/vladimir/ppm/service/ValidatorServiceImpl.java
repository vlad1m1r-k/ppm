package com.vladimir.ppm.service;

import org.springframework.stereotype.Service;

@Service
public class ValidatorServiceImpl implements ValidatorService {
    @Override
    public boolean validateCrypto(String key, String data) {
        return validateString(key) && validateString(data);
    }

    private boolean validateString(String str) {
        return str != null && str.length() > 0;
    }
}

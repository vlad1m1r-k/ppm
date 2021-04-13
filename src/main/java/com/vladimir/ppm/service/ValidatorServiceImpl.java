package com.vladimir.ppm.service;

import org.springframework.stereotype.Service;

@Service
public class ValidatorServiceImpl implements ValidatorService {
    @Override
    public boolean validateCrypto(String key, String data) {
        return validateString(key) && validateString(data);
    }

    @Override
    public boolean validateSrvKeyLT(int srvKeyLT) {
        return srvKeyLT > 0 && srvKeyLT <= 366;
    }

    @Override
    public boolean validateUsrTknLT(int usrTknLT) {
        return usrTknLT > 0 && usrTknLT <= 59;
    }

    @Override
    public boolean validatePwdMinLength(int length) {
        return length >= 3 && length <= 20;
    }

    private boolean validateString(String str) {
        return str != null && str.length() > 0;
    }
}

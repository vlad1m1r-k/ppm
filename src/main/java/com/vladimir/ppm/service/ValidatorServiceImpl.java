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

    @Override
    public boolean validatePwdLength(String pwd, int length) {
        return validateString(pwd) && pwd.length() >= length;
    }

    @Override
    public boolean validatePwdComplexity(String pwd) {
        boolean upperCase = false;
        boolean lowerCase = false;
        boolean digit = false;
        for (char ch: pwd.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                upperCase = true;
            }
            if (Character.isLowerCase(ch)) {
                lowerCase = true;
            }
            if (Character.isDigit(ch)) {
                digit = true;
            }
            if (upperCase && lowerCase && digit) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validatePwdSpecialChar(String pwd) {
        return pwd.matches(".*[`~!@#$%^&*()_\\-+=|\\\\/{}\\[\\]:;\"',.]+.*");
    }

    private boolean validateString(String str) {
        return str != null && str.length() > 0;
    }
}

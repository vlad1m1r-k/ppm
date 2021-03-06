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

    @Override
    public boolean validatePwdLoginIncluded(String pwd, String login) {
        return !pwd.contains(login);
    }

    @Override
    public boolean validatePwdRepeatedChars(String pwd) {
        return !pwd.matches(".*(.)\\1{2,}.*");
    }

    @Override
    public boolean validateIncLoginAtt(int incLogAtt) {
        return incLogAtt > 0 && incLogAtt <= 20;
    }

    @Override
    public boolean validateIpBanTime(int banTime) {
        return banTime >= 0 && banTime <= 20;
    }

    @Override
    public boolean validateIncPassAtt(int passAtt) {
        return validateIncLoginAtt(passAtt);
    }

    @Override
    public boolean validateIpOrSubnet(String subnet) {
        return subnet != null && subnet.matches("^(?:(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(?!$)|$|(\\/([1-2][0-9]|3[0-2]|[0-9])))){4}$");
    }

    @Override
    public boolean validateString(String str) {
        return str != null && str.length() > 0;
    }
}

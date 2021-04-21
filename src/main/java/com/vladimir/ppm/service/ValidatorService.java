package com.vladimir.ppm.service;

public interface ValidatorService {
    boolean validateString(String str);
    boolean validateCrypto(String key, String data);
    boolean validateSrvKeyLT(int srvKeyLT);
    boolean validateUsrTknLT(int usrTknLT);
    boolean validatePwdMinLength(int length);
    boolean validatePwdLength(String pwd, int length);
    boolean validatePwdComplexity(String pwd);
    boolean validatePwdSpecialChar(String pwd);
    boolean validatePwdLoginIncluded(String pwd, String login);
    boolean validatePwdRepeatedChars(String pwd);
}

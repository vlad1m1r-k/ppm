package com.vladimir.ppm.service;

public interface ValidatorService {
    boolean validateCrypto(String key, String data);
    boolean validateSrvKeyLT(int srvKeyLT);
    boolean validateUsrTknLT(int usrTknLT);
}

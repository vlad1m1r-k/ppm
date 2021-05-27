package com.vladimir.ppm.service;

public interface SecurityService {
    boolean isIpBanned(String ip);
    void registerLoginAttempt(String ip, boolean  success);
    void registerPasswordAttempt(long userId, boolean success);
}

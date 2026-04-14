package com.volodymyr.ppm.service;

import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.domain.User;

public interface TokenService {
    Token getToken(User user, String remoteAddr, String userAgent, boolean changePwd, boolean tfaApproved);
    String encrypt(Token token);
    Token decryptToken(String token);
    Token validateToken(User user, String token, String remoteAddr, String userAgent);
    Token validateToken(User user, String token, String remoteAddr, String userAgent, boolean changePwd, boolean validateTfaCode);
}

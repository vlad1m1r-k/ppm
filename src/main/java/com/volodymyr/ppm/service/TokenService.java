package com.volodymyr.ppm.service;

import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.domain.User;

public interface TokenService {
    Token getToken(User user, String remoteAddr, String userAgent, boolean changePwd);
    String encrypt(Token token);
    Token validateToken(String token, String remoteAddr, String userAgent);
    Token validateToken(String token, String remoteAddr, String userAgent, boolean changePwd);
}

package com.vladimir.ppm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;

public interface TokenService {
    Token getToken(User user, String remoteAddr, String userAgent, boolean changePwd);
    String encrypt(Token token);
    Token validateToken(String token, String remoteAddr, String userAgent) throws JsonProcessingException;
    Token validateToken(String token, String remoteAddr, String userAgent, boolean changePwd) throws JsonProcessingException;
}

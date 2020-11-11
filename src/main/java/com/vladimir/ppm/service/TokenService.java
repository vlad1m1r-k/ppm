package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;

public interface TokenService {
    Token getToken(User user, String remoteAddr, String userAgent);
    String encrypt(Token token);
}

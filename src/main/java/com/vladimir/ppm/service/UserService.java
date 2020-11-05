package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.TokenDto;

public interface UserService {
    public TokenDto login(String login, String password, String frontPublicKey, String remoteAddr, String userAgent);
}

package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.TokenDto;

public interface UserService {
    TokenDto login(String login, String password, String remoteAddr, String userAgent);
}

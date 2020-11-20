package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.TokenDto;

import java.util.Set;

public interface UserService {
    TokenDto login(String login, String password, String remoteAddr, String userAgent);
    Set<Group> getGroups(Token token);
}

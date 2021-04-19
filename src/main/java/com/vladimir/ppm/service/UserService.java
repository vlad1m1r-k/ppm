package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    TokenDto login(String login, String password, String remoteAddr, String userAgent);
    TokenDto renewToken(Token token);
    Set<Group> getGroups(Token token);
    boolean isAdmin(Token token);
    MessageDto changePassword(Token token, String newPwd);
    List<UserDto> getUsers(Token token);
    boolean isUserEnabled(Token token);
}

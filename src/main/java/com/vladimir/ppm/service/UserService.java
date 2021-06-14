package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.domain.UserStatus;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    TokenDto login(String login, String password, String remoteAddr, String userAgent);
    TokenDto renewToken(Token token);
    User getUserById(long userId);
    Set<Group> getGroups(Token token);
    boolean isAdmin(Token token);
    MessageDto changePassword(Token token, String newPwd);
    List<UserDto> getUsers(Token token, String sort);
    boolean isUserEnabled(Token token);
    MessageDto addUser(Token token, String login, String pwd, UserStatus status);
    MessageDto editUser(Token token, long userId, String login, String pwd, UserStatus status);
    MessageDto deleteUser(Token token, long userId);
    MessageDto addAllowedIp(Token token, long userId, String ip);
    List<String> getAllowedIp(Token token, long userId);
    void removeAllowedIp(Token token, long userId, String ip);
}

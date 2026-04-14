package com.volodymyr.ppm.service;

import com.volodymyr.ppm.domain.Group;
import com.volodymyr.ppm.domain.PwdGenSettings;
import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.domain.User;
import com.volodymyr.ppm.domain.UserStatus;
import com.volodymyr.ppm.dto.MessageDto;
import com.volodymyr.ppm.dto.TokenDto;
import com.volodymyr.ppm.dto.UserDto;

import dev.samstevens.totp.exceptions.QrGenerationException;

import java.util.List;
import java.util.Set;

public interface UserService {
    TokenDto login(String login, String password, String remoteAddr, String userAgent) throws QrGenerationException;
    TokenDto renewToken(Token token);
    TokenDto verifyTfaCode(Token token, String code) throws QrGenerationException;
    User getUserById(long userId);
    User getUser(Token token);
    Set<Group> getGroups(Token token);
    boolean isAdmin(Token token);
    MessageDto changePassword(Token token, String newPwd, String oldPwd);
    List<UserDto> getUsers(Token token, String sort);
    boolean isUserEnabled(Token token);
    MessageDto addUser(Token token, String login, String pwd, UserStatus status, boolean changePwd);
    MessageDto editUser(Token token, long userId, String login, String pwd, UserStatus status, boolean changePwd, boolean tfaStatus);
    MessageDto deleteUser(Token token, long userId);
    MessageDto addAllowedIp(Token token, long userId, String ip);
    List<String> getAllowedIp(Token token, long userId);
    void removeAllowedIp(Token token, long userId, String ip);
    PwdGenSettings getPwdGenSettings(Token token);
    MessageDto setPwdGenSettings(Token token, int pwdLength, boolean numbers, boolean symbols);
}

package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.DynamicListEntryDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.SettingsDto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

public interface SettingsService {
    SettingsDto getSettings(Token token);
    MessageDto saveSettings(Token token, int serverKeyLifeTime, int tokenLifeTime, int pwdMinLength,
                            boolean pwdComplexity, boolean pwdSpecialChar) throws NoSuchAlgorithmException, NoSuchProviderException;
    MessageDto saveSecuritySettings(Token token, int incorrectLoginAttempts, int ipBanTimeDays, int incorrectPasswdAttempts);
    MessageDto addIpToBlackList(Token token, String ip);
    MessageDto addIpToWhiteList(Token token, String ip);
    List<String> getIpBlackList(Token token);
    List<String> getIpWhiteList(Token token);
    List<DynamicListEntryDto> getDynamicList(Token token);
    MessageDto removeIpFromBlackList(Token token, String ip);
    MessageDto removeIpFromWhiteList(Token token, String ip);
    MessageDto removeIpFromDynamicList(Token token, String ip);
}

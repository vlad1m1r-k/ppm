package com.volodymyr.ppm.service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.dto.DynamicListEntryDto;
import com.volodymyr.ppm.dto.MessageDto;
import com.volodymyr.ppm.dto.SettingsDto;

public interface SettingsService {
    SettingsDto getSettings(Token token);
    MessageDto saveSettings(Token token, int serverKeyLifeTime, int tokenLifeTime, int tfaTokenLifeTime, int pwdMinLength,
                            boolean pwdComplexity, boolean pwdSpecialChar, int logLifeTime) throws NoSuchAlgorithmException, NoSuchProviderException;
    MessageDto saveSecuritySettings(Token token, int incorrectLoginAttempts, int ipBanTimeDays, int incorrectPasswdAttempts, int tfaRequirePeriod);
    MessageDto addIpToBlackList(Token token, String ip);
    MessageDto addIpToWhiteList(Token token, String ip);
    List<String> getIpBlackList(Token token);
    List<String> getIpWhiteList(Token token);
    List<DynamicListEntryDto> getDynamicList(Token token);
    MessageDto removeIpFromBlackList(Token token, String ip);
    MessageDto removeIpFromWhiteList(Token token, String ip);
    MessageDto removeIpFromDynamicList(Token token, String ip);
}

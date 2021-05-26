package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.SettingsDto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface SettingsService {
    SettingsDto getSettings(Token token);
    MessageDto saveSettings(Token token, int serverKeyLifeTime, int tokenLifeTime, int pwdMinLength,
                            boolean pwdComplexity, boolean pwdSpecialChar) throws NoSuchAlgorithmException, NoSuchProviderException;
    MessageDto saveSecuritySettings(Token token, int incorrectLoginAttempts, int ipBanTimeDays, int incorrectPasswdAttempts);
}

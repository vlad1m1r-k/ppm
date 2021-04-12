package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.SettingsDto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface SettingsService {
    SettingsDto getSettings(Token token);
    MessageDto saveSettings(Token token, int serverKeyLifeTime, int tokenLifeTime) throws NoSuchAlgorithmException, NoSuchProviderException;
}

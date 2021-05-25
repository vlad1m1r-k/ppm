package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.SettingsDto;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final UserService userService;
    private final SettingsProvider settingsProvider;
    private final ValidatorService validatorService;
    private final CryptoProvider cryptoProvider;

    public SettingsServiceImpl(UserService userService, SettingsProvider settingsProvider, ValidatorService validatorService,
                               CryptoProvider cryptoProvider) {
        this.userService = userService;
        this.settingsProvider = settingsProvider;
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
    }

    @Override
    public SettingsDto getSettings(Token token) {
        if (userService.isAdmin(token)) {
            return SettingsDto.builder()
                    .serverKeyLifeTimeDays(settingsProvider.getServerKeyLifeTimeDays())
                    .tokenLifeTimeMinutes(settingsProvider.getTokenLifeTimeMinutes())
                    .pwdMinLength(settingsProvider.getPwdMinLength())
                    .pwdComplexity(settingsProvider.getPwdComplexity())
                    .pwdSpecialChar(settingsProvider.getPwdSpecialChar())
                    .incorrectLoginAttempts(settingsProvider.getIncorrectLoginAttempts())
                    .ipBanTimeDays(settingsProvider.getIpBanTimeDays())
                    .incorrectPasswdAttempts(settingsProvider.getIncorrectPasswdAttempts())
                    .build();
        }
        return null;
    }

    @Override
    public MessageDto saveSettings(Token token, int serverKeyLifeTime, int tokenLifeTime, int pwdMinLength,
                                   boolean pwdComplexity, boolean pwdSpecialChar) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (userService.isAdmin(token) && validatorService.validateSrvKeyLT(serverKeyLifeTime) &&
                validatorService.validateUsrTknLT(tokenLifeTime) && validatorService.validatePwdMinLength(pwdMinLength)) {
            if (settingsProvider.getServerKeyLifeTimeDays() != serverKeyLifeTime ) {
                if (settingsProvider.getServerKeyLifeTimeDays() > serverKeyLifeTime) {
                    settingsProvider.setServerKeyLifeTimeDays(serverKeyLifeTime);
                    cryptoProvider.generateServerKeypair();
                }
                settingsProvider.setServerKeyLifeTimeDays(serverKeyLifeTime);
            }
            if (settingsProvider.getTokenLifeTimeMinutes() != tokenLifeTime) {
                settingsProvider.setTokenLifeTimeMinutes(tokenLifeTime);
            }
            if (settingsProvider.getPwdMinLength() != pwdMinLength) {
                settingsProvider.setPwdMinLength(pwdMinLength);
            }
            if (settingsProvider.getPwdComplexity() != pwdComplexity) {
                settingsProvider.setPwdComplexity(pwdComplexity);
            }
            if (settingsProvider.getPwdSpecialChar() != pwdSpecialChar) {
                settingsProvider.setPwdSpecialChar(pwdSpecialChar);
            }
            return MessageDto.builder().build();
        }
        return MessageDto.builder().message("srve1").build();
    }
}

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
                    .build();
        }
        return null;
    }

    @Override
    public MessageDto saveSettings(Token token, int serverKeyLifeTime, int tokenLifeTime) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (userService.isAdmin(token) && validatorService.validateSrvKeyLT(serverKeyLifeTime) && validatorService.validateUsrTknLT(tokenLifeTime)) {
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
            return MessageDto.builder().build();
        }
        return MessageDto.builder().message("srve1").build();
    }
}

package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.DynamicListEntryDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.SettingsDto;
import com.vladimir.ppm.provider.CryptoProvider;
import com.vladimir.ppm.provider.SecurityProvider;
import com.vladimir.ppm.provider.SettingsProvider;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final UserService userService;
    private final SettingsProvider settingsProvider;
    private final ValidatorService validatorService;
    private final CryptoProvider cryptoProvider;
    private final SecurityProvider securityProvider;

    public SettingsServiceImpl(UserService userService, SettingsProvider settingsProvider, ValidatorService validatorService,
                               CryptoProvider cryptoProvider, SecurityProvider securityProvider) {
        this.userService = userService;
        this.settingsProvider = settingsProvider;
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
        this.securityProvider = securityProvider;
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
            if (settingsProvider.getServerKeyLifeTimeDays() != serverKeyLifeTime) {
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

    @Override
    public MessageDto saveSecuritySettings(Token token, int incorrectLoginAttempts, int ipBanTimeDays, int incorrectPasswdAttempts) {
        if (userService.isAdmin(token) && validatorService.validateIncLoginAtt(incorrectLoginAttempts)
                && validatorService.validateIpBanTime(ipBanTimeDays) && validatorService.validateIncPassAtt(incorrectPasswdAttempts)) {
            if (settingsProvider.getIncorrectLoginAttempts() != incorrectLoginAttempts) {
                settingsProvider.setIncorrectLoginAttempts(incorrectLoginAttempts);
            }
            if (settingsProvider.getIpBanTimeDays() != ipBanTimeDays) {
                settingsProvider.setIpBanTimeDays(ipBanTimeDays);
            }
            if (settingsProvider.getIncorrectPasswdAttempts() != incorrectPasswdAttempts) {
                settingsProvider.setIncorrectPasswdAttempts(incorrectPasswdAttempts);
            }
            return MessageDto.builder().build();
        }
        return MessageDto.builder().message("sece1").build();
    }

    @Override
    public MessageDto addIpToBlackList(Token token, String ip) {
        if (userService.isAdmin(token) && validatorService.validateIpOrSubnet(ip)) {
            settingsProvider.addIpToBlackList(ip);
            return MessageDto.builder().build();
        }
        return MessageDto.builder().message("sece2").build();
    }

    @Override
    public MessageDto addIpToWhiteList(Token token, String ip) {
        if (userService.isAdmin(token) && validatorService.validateIpOrSubnet(ip)) {
            settingsProvider.addIpToWhiteList(ip);
            return MessageDto.builder().build();
        }
        return MessageDto.builder().message("sece2").build();
    }

    @Override
    public List<String> getIpBlackList(Token token) {
        if (userService.isAdmin(token)) {
            return settingsProvider.getIpBlacklist().stream().sorted().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getIpWhiteList(Token token) {
        if (userService.isAdmin(token)) {
            return settingsProvider.getIpWhiteList().stream().sorted().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<DynamicListEntryDto> getDynamicList(Token token) {
        if (userService.isAdmin(token)) {
            return securityProvider.getDynamicIpMap().entrySet().stream()
                    .filter(e -> e.getValue().isBanned())
                    .map(e -> DynamicListEntryDto.builder()
                            .ip(e.getKey())
                            .expire(e.getValue().getUnbanTime())
                            .build())
                    .sorted(Comparator.comparing(DynamicListEntryDto::getIp))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public MessageDto removeIpFromBlackList(Token token, String ip) {
        if (userService.isAdmin(token) && validatorService.validateString(ip)) {
            settingsProvider.removeIpFromBlackList(ip);
        }
        return MessageDto.builder().build();
    }

    @Override
    public MessageDto removeIpFromWhiteList(Token token, String ip) {
        if (userService.isAdmin(token) && validatorService.validateString(ip)) {
            settingsProvider.removeIpFromWhiteList(ip);
        }
        return MessageDto.builder().build();
    }

    @Override
    public MessageDto removeIpFromDynamicList(Token token, String ip) {
        if (userService.isAdmin(token) && validatorService.validateString(ip)) {
            securityProvider.getDynamicIpMap().remove(ip);
        }
        return MessageDto.builder().build();
    }
}

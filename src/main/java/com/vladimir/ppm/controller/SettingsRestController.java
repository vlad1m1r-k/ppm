package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.SettingsDto;
import com.vladimir.ppm.provider.CryptoProvider;
import com.vladimir.ppm.service.DatabaseService;
import com.vladimir.ppm.service.SettingsService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.UserService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

@RestController
@RequestMapping("/settings")
public class SettingsRestController {
    private final ValidatorService validatorService;
    private final CryptoProvider cryptoProvider;
    private final TokenService tokenService;
    private final DatabaseService databaseService;
    private final SettingsService settingsService;
    private final UserService userService;
    private final ObjectMapper mapper;

    public SettingsRestController(ValidatorService validatorService, CryptoProvider cryptoProvider, TokenService tokenService,
                                  DatabaseService databaseService, SettingsService settingsService, UserService userService, ObjectMapper mapper) {
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
        this.tokenService = tokenService;
        this.databaseService = databaseService;
        this.settingsService = settingsService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping("/getSettings")
    public CryptoDto getSettings(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                SettingsDto settings = settingsService.getSettings(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, settings.toJson());
            }
        }
        return null;
    }

    @PostMapping("/saveSettings")
    public CryptoDto saveSettings(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException, NoSuchAlgorithmException, NoSuchProviderException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            int serverKeyLifeTime = json.get("serverKeyLifeTime").asInt();
            int tokenLifeTime = json.get("tokenLifeTime").asInt();
            int pwdMinLength = json.get("pwdMinLength").asInt();
            int logLifeTime = json.get("logLifeTime").asInt();
            boolean pwdComplexity = json.get("pwdComplexity").asBoolean();
            boolean pwdSpecialChar = json.get("pwdSpecialChar").asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.saveSettings(decryptedToken, serverKeyLifeTime, tokenLifeTime,
                        pwdMinLength, pwdComplexity, pwdSpecialChar, logLifeTime);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/saveSecuritySettings")
    public CryptoDto saveSecuritySettings(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            int incorrectLoginAttempts = json.get("incorrectLoginAttempts").asInt();
            int ipBanTimeDays = json.get("ipBanTimeDays").asInt();
            int incorrectPasswdAttempts = json.get("incorrectPasswdAttempts").asInt();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.saveSecuritySettings(decryptedToken, incorrectLoginAttempts, ipBanTimeDays, incorrectPasswdAttempts);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/dbStatus")
    public CryptoDto getDbStatus(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto status = databaseService.getDBStatus(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, status.toJson());
            }
        }
        return null;
    }

    @PostMapping("/keyGen")
    public CryptoDto generateKey(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto dbKey = databaseService.generateDbKey(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, dbKey.toJson());
            }
        }
        return null;
    }

    @PostMapping("/setKey")
    public CryptoDto setKey(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws IOException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String dbKey = json.get("key").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto answer = databaseService.installDbKey(decryptedToken, dbKey);
                return cryptoProvider.encrypt(publicKeyPEM, answer.toJson());
            }
        }
        return null;
    }

    @PostMapping("/addIpToBlackList")
    public CryptoDto addIpToBlackList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String ip = json.get("ip").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.addIpToBlackList(decryptedToken, ip);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/addIpToWhiteList")
    public CryptoDto addIpToWhiteList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String ip = json.get("ip").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.addIpToWhiteList(decryptedToken, ip);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getIpBlackList")
    public CryptoDto getIpBlackList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                List<String> ipSet = settingsService.getIpBlackList(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(ipSet));
            }
        }
        return null;
    }

    @PostMapping("/getIpWhiteList")
    public CryptoDto getIpWhiteList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                List<String> ipSet = settingsService.getIpWhiteList(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(ipSet));
            }
        }
        return null;
    }

    @PostMapping("/removeIpFromBlackList")
    public CryptoDto removeIpFromBlackList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String ip = json.get("ip").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.removeIpFromBlackList(decryptedToken, ip);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/removeIpFromWhiteList")
    public CryptoDto removeIpFromWhiteList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String ip = json.get("ip").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.removeIpFromWhiteList(decryptedToken, ip);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getDynamicList")
    public CryptoDto getDynamicList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(settingsService.getDynamicList(decryptedToken)));
            }
        }
        return null;
    }

    @PostMapping("/removeIpFromDynamicList")
    public CryptoDto removeIpFromDynamicList(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String ip = json.get("ip").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.removeIpFromDynamicList(decryptedToken, ip);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }
}

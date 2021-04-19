package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.SettingsDto;
import com.vladimir.ppm.service.CryptoProvider;
import com.vladimir.ppm.service.DatabaseService;
import com.vladimir.ppm.service.SettingsService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.UserService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
@RequestMapping("/settings")
public class SettingsRestController {
    private final ValidatorService validatorService;
    private final CryptoProvider cryptoProvider;
    private final TokenService tokenService;
    private final DatabaseService databaseService;
    private final SettingsService settingsService;
    private final UserService userService;

    public SettingsRestController(ValidatorService validatorService, CryptoProvider cryptoProvider, TokenService tokenService,
                                  DatabaseService databaseService, SettingsService settingsService, UserService userService) {
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
        this.tokenService = tokenService;
        this.databaseService = databaseService;
        this.settingsService = settingsService;
        this.userService = userService;
    }

    @PostMapping("/getSettings")
    public CryptoDto getSettings(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = new ObjectMapper().readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && !userService.isUserEnabled(decryptedToken)) {
                SettingsDto settings = settingsService.getSettings(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, settings.toJson());
            }
        }
        return null;
    }

    @PostMapping("/saveSettings")
    public CryptoDto saveSettings(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException, NoSuchAlgorithmException, NoSuchProviderException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = new ObjectMapper().readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            int serverKeyLifeTime = json.get("serverKeyLifeTime").asInt();
            int tokenLifeTime = json.get("tokenLifeTime").asInt();
            int pwdMinLength = json.get("pwdMinLength").asInt();
            boolean pwdComplexity = json.get("pwdComplexity").asBoolean();
            boolean pwdSpecialChar = json.get("pwdSpecialChar").asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && !userService.isUserEnabled(decryptedToken)) {
                MessageDto message = settingsService.saveSettings(decryptedToken, serverKeyLifeTime, tokenLifeTime,
                        pwdMinLength, pwdComplexity, pwdSpecialChar);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/dbStatus")
    public CryptoDto getDbStatus(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = new ObjectMapper().readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && !userService.isUserEnabled(decryptedToken)) {
                MessageDto status = databaseService.getDBStatus(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, status.toJson());
            }
        }
        return null;
    }

    @PostMapping("/keyGen")
    public CryptoDto generateKey(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = new ObjectMapper().readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && !userService.isUserEnabled(decryptedToken)) {
                MessageDto dbKey = databaseService.generateDbKey(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, dbKey.toJson());
            }
        }
        return null;
    }

    @PostMapping("setKey")
    public CryptoDto setKey(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws IOException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = new ObjectMapper().readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String dbKey = json.get("key").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && !userService.isUserEnabled(decryptedToken)) {
                MessageDto answer = databaseService.installDbKey(decryptedToken, dbKey);
                return cryptoProvider.encrypt(publicKeyPEM, answer.toJson());
            }
        }
        return null;
    }
}

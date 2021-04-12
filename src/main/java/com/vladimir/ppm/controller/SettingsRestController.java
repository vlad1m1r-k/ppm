package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.service.CryptoProvider;
import com.vladimir.ppm.service.DatabaseService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/settings")
public class SettingsRestController {
    private final ValidatorService validatorService;
    private final CryptoProvider cryptoProvider;
    private final TokenService tokenService;
    private final DatabaseService databaseService;

    public SettingsRestController(ValidatorService validatorService, CryptoProvider cryptoProvider, TokenService tokenService,
                                  DatabaseService databaseService) {
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
        this.tokenService = tokenService;
        this.databaseService = databaseService;
    }

    @PostMapping("/dbStatus")
    public CryptoDto getDbStatus(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = new ObjectMapper().readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
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
            if (decryptedToken != null) {
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
            if (decryptedToken != null) {
                MessageDto answer = databaseService.installDbKey(decryptedToken, dbKey);
                return cryptoProvider.encrypt(publicKeyPEM, answer.toJson());
            }
        }
        return null;
    }
}

package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.provider.CryptoProvider;
import com.vladimir.ppm.service.LoggerService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.UserService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/logs")
public class LogsRestController {
    private final ValidatorService validatorService;
    private final CryptoProvider cryptoProvider;
    private final TokenService tokenService;
    private final ObjectMapper mapper;
    private final UserService userService;
    private final LoggerService loggerService;

    public LogsRestController(ValidatorService validatorService, CryptoProvider cryptoProvider, TokenService tokenService,
                              ObjectMapper mapper, UserService userService, LoggerService loggerService) {
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
        this.tokenService = tokenService;
        this.mapper = mapper;
        this.userService = userService;
        this.loggerService = loggerService;
    }

    @PostMapping("/getLogs")
    public CryptoDto getLogs(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            int page = json.get("page").intValue();
            int size = json.get("size").intValue();
            String direction = json.get("direction").textValue();
            String field = json.get("field").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(loggerService.getLogs(decryptedToken, page, size, direction, field)));
            }
        }
        return null;
    }

    @PostMapping("/search")
    public CryptoDto search(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            int page = json.get("page").intValue();
            int size = json.get("size").intValue();
            String direction = json.get("direction").textValue();
            String field = json.get("field").textValue();
            String text = json.get("text").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(loggerService
                        .search(decryptedToken, page, size, direction, field, text)));
            }
        }
        return null;
    }
}

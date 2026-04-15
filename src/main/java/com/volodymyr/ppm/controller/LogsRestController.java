package com.volodymyr.ppm.controller;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.domain.User;
import com.volodymyr.ppm.dto.CryptoDto;
import com.volodymyr.ppm.provider.CryptoProvider;
import com.volodymyr.ppm.service.LoggerService;
import com.volodymyr.ppm.service.TokenService;
import com.volodymyr.ppm.service.UserService;
import com.volodymyr.ppm.service.ValidatorService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
    public CryptoDto getLogs(@RequestParam String key, @RequestParam String data, HttpServletRequest request, HttpSession session) {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").asString();
            String publicKeyPEM = json.get("publicKey").asString();
            int page = json.get("page").intValue();
            int size = json.get("size").intValue();
            String direction = json.get("direction").asString();
            String field = json.get("field").asString();
            User user = userService.getUser(tokenService.decryptToken(token));
            Token decryptedToken = tokenService.validateToken(user, token, request.getRemoteAddr(), request.getHeader("User-Agent"), session.getId());
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(loggerService.getLogs(decryptedToken, page, size, direction, field)));
            }
        }
        return null;
    }

    @PostMapping("/search")
    public CryptoDto search(@RequestParam String key, @RequestParam String data, HttpServletRequest request, HttpSession session) {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").asString();
            String publicKeyPEM = json.get("publicKey").asString();
            int page = json.get("page").intValue();
            int size = json.get("size").intValue();
            String direction = json.get("direction").asString();
            String field = json.get("field").asString();
            String text = json.get("text").asString();
            User user = userService.getUser(tokenService.decryptToken(token));
            Token decryptedToken = tokenService.validateToken(user, token, request.getRemoteAddr(), request.getHeader("User-Agent"), session.getId());
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(loggerService
                        .search(decryptedToken, page, size, direction, field, text)));
            }
        }
        return null;
    }
}

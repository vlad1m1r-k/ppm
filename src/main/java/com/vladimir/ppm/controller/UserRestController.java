package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.service.CryptoProvider;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.UserService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserRestController {
    private final CryptoProvider cryptoProvider;
    private final UserService userService;
    private final ValidatorService validatorService;
    private final TokenService tokenService;
    private final ObjectMapper mapper;

    public UserRestController(CryptoProvider cryptoProvider, UserService userService, ValidatorService validatorService,
                              TokenService tokenService, ObjectMapper mapper) {
        this.cryptoProvider = cryptoProvider;
        this.userService = userService;
        this.validatorService = validatorService;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public CryptoDto login(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String login = json.get("login").textValue();
            String password = json.get("password").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            TokenDto tokenDto = userService.login(login, password, request.getRemoteAddr(), request.getHeader("User-Agent"));
            return cryptoProvider.encrypt(publicKeyPEM, tokenDto.toJson());
        }
        return null;
    }

    @PostMapping("/renewToken")
    public CryptoDto renewToken(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                TokenDto tokenDto = userService.renewToken(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, tokenDto.toJson());
            }
        }
        return null;
    }

    @PostMapping("/setPwd")
    public CryptoDto setPwd(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String newPwd = json.get("pwd").asText();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = userService.changePassword(decryptedToken, newPwd);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }
}

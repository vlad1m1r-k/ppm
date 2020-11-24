package com.vladimir.ppm.controller;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.service.CryptoProviderService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.UserService;
import com.vladimir.ppm.service.ValidatorService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserRestController {
    private final CryptoProviderService cryptoProviderService;
    private final UserService userService;
    private final ValidatorService validatorService;
    private final TokenService tokenService;

    public UserRestController(CryptoProviderService cryptoProviderService, UserService userService, ValidatorService validatorService,
                              TokenService tokenService) {
        this.cryptoProviderService = cryptoProviderService;
        this.userService = userService;
        this.validatorService = validatorService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public CryptoDto login(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProviderService.decrypt(key, data));
            String login = json.getString("login");
            String password = json.getString("password");
            String publicKeyPEM = json.getString("publicKey");
            TokenDto tokenDto = userService.login(login, password, request.getRemoteAddr(), request.getHeader("User-Agent"));
            return cryptoProviderService.encrypt(publicKeyPEM, tokenDto.toJson());
        }
        return null;
    }

    @PostMapping("/renewToken")
    public CryptoDto renewToken(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProviderService.decrypt(key, data));
            String token = json.getString("token");
            String publicKeyPEM = json.getString("publicKey");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                TokenDto tokenDto = userService.renewToken(decryptedToken);
                return cryptoProviderService.encrypt(publicKeyPEM, tokenDto.toJson());
            }
        }
        return null;
    }
}

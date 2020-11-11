package com.vladimir.ppm.controller;

import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.service.CryptoProviderService;
import com.vladimir.ppm.service.UserService;
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

    public UserRestController(CryptoProviderService cryptoProviderService, UserService userService) {
        this.cryptoProviderService = cryptoProviderService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public CryptoDto login(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        JSONObject json = new JSONObject(cryptoProviderService.decrypt(key, data));
        String login = json.getString("login");
        String password = json.getString("password");
        String publicKeyPEM = json.getString("publicKey");
        TokenDto tokenDto = userService.login(login, password, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return cryptoProviderService.encrypt(publicKeyPEM, tokenDto.toJson());
    }
}

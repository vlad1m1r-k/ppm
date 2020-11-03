package com.vladimir.ppm.controller;

import com.vladimir.ppm.service.CryptoProviderService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserRestController {
    private CryptoProviderService cryptoProviderService;

    public UserRestController(CryptoProviderService cryptoProviderService) {
        this.cryptoProviderService = cryptoProviderService;
    }

    @PostMapping("/login")
    public boolean login(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        JSONObject json = new JSONObject(cryptoProviderService.decrypt(key, data));
        String login = json.getString("login");
        String password = json.getString("password");
        String publicKeyPEM = json.getString("publicKey");
        System.out.println(request.getRemoteAddr());
        System.out.println(request.getHeader("User-Agent"));
        //TODO
        return true;
    }
}

package com.vladimir.ppm.controller;

import com.vladimir.ppm.service.CryptoProviderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserRestController {
    private CryptoProviderService cryptoProviderService;

    public UserRestController(CryptoProviderService cryptoProviderService) {
        this.cryptoProviderService = cryptoProviderService;
    }

    @PostMapping("/login")
    public boolean login(@RequestParam String key, @RequestParam String data) {
        //TODO
        cryptoProviderService.decrypt(key, data);
        return true;
    }
}

package com.vladimir.ppm.controller;

import com.vladimir.ppm.dto.PublicKeyDto;
import com.vladimir.ppm.service.CryptoProviderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crypto")
public class CryptoRestController {
    private CryptoProviderService cryptoProviderService;

    public CryptoRestController(CryptoProviderService cryptoProviderService) {
        this.cryptoProviderService = cryptoProviderService;
    }

    @GetMapping("/getKey")
    public PublicKeyDto getPublicKey() {
        return cryptoProviderService.getPublicKey();
    }
}

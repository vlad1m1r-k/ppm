package com.vladimir.ppm.controller;

import com.vladimir.ppm.dto.PublicKeyDto;
import com.vladimir.ppm.service.CryptoProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crypto")
public class CryptoRestController {
    private CryptoProvider cryptoProvider;

    public CryptoRestController(CryptoProvider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    @GetMapping("/getKey")
    public PublicKeyDto getPublicKey() {
        return cryptoProvider.getPublicKey();
    }
}

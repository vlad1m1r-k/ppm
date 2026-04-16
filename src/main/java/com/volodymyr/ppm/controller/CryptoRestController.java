package com.volodymyr.ppm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volodymyr.ppm.dto.PublicKeyDto;
import com.volodymyr.ppm.provider.CryptoProvider;

@RestController
@RequestMapping("/crypto")
public class CryptoRestController {
    private final CryptoProvider cryptoProvider;

    public CryptoRestController(CryptoProvider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    @GetMapping("/getKey")
    public PublicKeyDto getPublicKey() {
        return cryptoProvider.getPublicKey();
    }
}

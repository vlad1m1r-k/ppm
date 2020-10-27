package com.vladimir.ppm.controller;

import com.vladimir.ppm.service.CryptoProviderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crypto")
public class CryptoRestController {
    private CryptoProviderService cryptoProviderService;

    public CryptoRestController(CryptoProviderService cryptoProviderService) {
        this.cryptoProviderService = cryptoProviderService;
    }

    public String getPublicKey() {

    }
}

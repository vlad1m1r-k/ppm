package com.vladimir.ppm.controller;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.service.CryptoProviderService;
import com.vladimir.ppm.service.SettingsService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.ValidatorService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/settings")
public class SettingsRestController {
    private final ValidatorService validatorService;
    private final CryptoProviderService cryptoProviderService;
    private final TokenService tokenService;
    private final SettingsService settingsService;

    public SettingsRestController(ValidatorService validatorService, CryptoProviderService cryptoProviderService,
                                  TokenService tokenService, SettingsService settingsService) {
        this.validatorService = validatorService;
        this.cryptoProviderService = cryptoProviderService;
        this.tokenService = tokenService;
        this.settingsService = settingsService;
    }

    @PostMapping("/dbStatus")
    public CryptoDto getDbStatus(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProviderService.decrypt(key, data));
            String token = json.getString("token");
            String publicKeyPEM = json.getString("publicKey");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto status = settingsService.getDbStatus(decryptedToken);
                return cryptoProviderService.encrypt(publicKeyPEM, status.toJson());
            }
        }
        return null;
    }
}

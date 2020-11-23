package com.vladimir.ppm.controller;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.service.ContainerService;
import com.vladimir.ppm.service.CryptoProviderService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.ValidatorService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/container")
public class ContainerRestController {
    private final ValidatorService validatorService;
    private final CryptoProviderService cryptoProviderService;
    private final TokenService tokenService;
    private final ContainerService containerService;

    public ContainerRestController(ValidatorService validatorService, CryptoProviderService cryptoProviderService,
                                   TokenService tokenService, ContainerService containerService) {
        this.validatorService = validatorService;
        this.cryptoProviderService = cryptoProviderService;
        this.tokenService = tokenService;
        this.containerService = containerService;
    }

    @PostMapping("/getTree")
    public CryptoDto getTree(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProviderService.decrypt(key, data));
            String token = json.getString("token");
            String publicKeyPEM = json.getString("publicKey");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                ContainerDto tree = containerService.getTree(decryptedToken);
                return cryptoProviderService.encrypt(publicKeyPEM, tree.toJson());
            }
        }
        return null;
    }
}

package com.vladimir.ppm.controller;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.service.ContainerService;
import com.vladimir.ppm.service.CryptoProvider;
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
    private final CryptoProvider cryptoProvider;
    private final TokenService tokenService;
    private final ContainerService containerService;

    public ContainerRestController(ValidatorService validatorService, CryptoProvider cryptoProvider,
                                   TokenService tokenService, ContainerService containerService) {
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
        this.tokenService = tokenService;
        this.containerService = containerService;
    }

    @PostMapping("/getTree")
    public CryptoDto getTree(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String token = json.getString("token");
            String publicKeyPEM = json.getString("publicKey");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                ContainerDto tree = containerService.getTree(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, tree.toJson());
            }
        }
        return null;
    }

    @PostMapping("/move")
    public CryptoDto move(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long itemId = json.getLong("item");
            long moveToId = json.getLong("moveTo");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.moveContainer(decryptedToken, itemId, moveToId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/add")
    public CryptoDto add(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long parentId = json.getLong("parent");
            String name = json.getString("name");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.add(decryptedToken, parentId, name);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/delete")
    public CryptoDto delete(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long itemId = json.getLong("item");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.delete(decryptedToken, itemId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/rename")
    public CryptoDto rename(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long itemId = json.getLong("item");
            String name = json.getString("name");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.rename(decryptedToken, itemId, name);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }
}

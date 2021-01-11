package com.vladimir.ppm.controller;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.PasswordDto;
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

    @PostMapping("/addNote")
    public CryptoDto addNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long parentId = json.getLong("parent");
            String name = json.getString("name");
            String text = json.getString("text");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.addNote(decryptedToken, parentId, name, text);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getNote")
    public CryptoDto getNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long noteId = json.getLong("note");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.getNote(decryptedToken, noteId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/editNote")
    public CryptoDto editNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long noteId = json.getLong("note");
            String name = json.getString("name");
            String text = json.getString("text");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.editNote(decryptedToken, noteId, name, text);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/removeNote")
    public CryptoDto removeNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long noteId = json.getLong("note");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.removeNote(decryptedToken, noteId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/addPasswd")
    public CryptoDto addPasswd(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long parentId = json.getLong("parent");
            String name = json.getString("name");
            String login = json.getString("login");
            String pass = json.getString("pass");
            String note = json.getString("note");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.addPasswd(decryptedToken, parentId, name, login, pass, note);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getPwdEnv")
    public CryptoDto getPwdEnv(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long pwdId = json.getLong("pwd");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                PasswordDto passwd = containerService.getPwdEnv(decryptedToken, pwdId);
                return cryptoProvider.encrypt(publicKeyPEM, passwd.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getPwdBody")
    public CryptoDto getPwdBody(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long pwdId = json.getLong("pwd");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                PasswordDto passwd = containerService.getPwdBody(decryptedToken, pwdId);
                return cryptoProvider.encrypt(publicKeyPEM, passwd.toJson());
            }
        }
        return null;
    }

    @PostMapping("/editPassword")
    public CryptoDto editPassword(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long pwdId = json.getLong("pwd");
            String name = json.getString("name");
            String login = json.getString("login");
            String pass = json.getString("pass");
            String note = json.getString("note");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.editPassword(decryptedToken, pwdId, name, login, pass, note);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/removePassword")
    public CryptoDto removePassword(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long pwdId = json.getLong("pwd");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.removePassword(decryptedToken, pwdId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getDeletedItems")
    public CryptoDto getDeletedItems(@RequestParam String key, @RequestParam String data, HttpServletRequest request) {
        if (validatorService.validateCrypto(key, data)) {
            JSONObject json = new JSONObject(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.getString("publicKey");
            String token = json.getString("token");
            long containerId = json.getLong("item");
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                ContainerDto container = containerService.getDeletedItems(decryptedToken, containerId);
                return cryptoProvider.encrypt(publicKeyPEM, container.toJson());
            }
        }
        return null;
    }
}

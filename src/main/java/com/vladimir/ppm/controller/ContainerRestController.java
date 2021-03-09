package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.PasswordDto;
import com.vladimir.ppm.service.ContainerService;
import com.vladimir.ppm.service.CryptoProvider;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/container")
public class ContainerRestController {
    private final ValidatorService validatorService;
    private final CryptoProvider cryptoProvider;
    private final TokenService tokenService;
    private final ContainerService containerService;
    private final ObjectMapper mapper;

    public ContainerRestController(ValidatorService validatorService, CryptoProvider cryptoProvider,
                                   TokenService tokenService, ContainerService containerService, ObjectMapper mapper) {
        this.validatorService = validatorService;
        this.cryptoProvider = cryptoProvider;
        this.tokenService = tokenService;
        this.containerService = containerService;
        this.mapper = mapper;
    }

    @PostMapping("/getTree")
    public CryptoDto getTree(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                ContainerDto tree = containerService.getTree(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, tree.toJson());
            }
        }
        return null;
    }

    @PostMapping("/move")
    public CryptoDto move(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long itemId = json.get("item").longValue();
            long moveToId = json.get("moveTo").longValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.moveContainer(decryptedToken, itemId, moveToId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/add")
    public CryptoDto add(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long parentId = json.get("parent").longValue();
            String name = json.get("name").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.add(decryptedToken, parentId, name);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/delete")
    public CryptoDto delete(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long itemId = json.get("item").longValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.delete(decryptedToken, itemId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/rename")
    public CryptoDto rename(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long itemId = json.get("item").longValue();
            String name = json.get("name").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.rename(decryptedToken, itemId, name);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/addNote")
    public CryptoDto addNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long parentId = json.get("parent").longValue();
            String name = json.get("name").textValue();
            String text = json.get("text").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.addNote(decryptedToken, parentId, name, text);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getNote")
    public CryptoDto getNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long noteId = json.get("note").longValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.getNote(decryptedToken, noteId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/editNote")
    public CryptoDto editNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long noteId = json.get("note").longValue();
            String name = json.get("name").textValue();
            String text = json.get("text").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.editNote(decryptedToken, noteId, name, text);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/removeNote")
    public CryptoDto removeNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long noteId = json.get("note").longValue();
            boolean permanent = Optional.ofNullable(json.get("permanent")).orElse(new ObjectMapper().createObjectNode().booleanNode(false)).asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.removeNote(decryptedToken, noteId, permanent);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/addPasswd")
    public CryptoDto addPasswd(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long parentId = json.get("parent").longValue();
            String name = json.get("name").textValue();
            String login = json.get("login").textValue();
            String pass = json.get("pass").textValue();
            String note = json.get("note").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.addPasswd(decryptedToken, parentId, name, login, pass, note);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getPwdEnv")
    public CryptoDto getPwdEnv(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long pwdId = json.get("pwd").longValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                PasswordDto passwd = containerService.getPwdEnv(decryptedToken, pwdId);
                return cryptoProvider.encrypt(publicKeyPEM, passwd.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getPwdBody")
    public CryptoDto getPwdBody(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long pwdId = json.get("pwd").longValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                PasswordDto passwd = containerService.getPwdBody(decryptedToken, pwdId);
                return cryptoProvider.encrypt(publicKeyPEM, passwd.toJson());
            }
        }
        return null;
    }

    @PostMapping("/editPassword")
    public CryptoDto editPassword(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long pwdId = json.get("pwd").longValue();
            String name = json.get("name").textValue();
            String login = json.get("login").textValue();
            String pass = json.get("pass").textValue();
            String note = json.get("note").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.editPassword(decryptedToken, pwdId, name, login, pass, note);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/removePassword")
    public CryptoDto removePassword(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long pwdId = json.get("pwd").longValue();
            boolean permanent = Optional.ofNullable(json.get("permanent")).orElse(new ObjectMapper().createObjectNode().booleanNode(false)).asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.removePassword(decryptedToken, pwdId, permanent);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getDeletedItems")
    public CryptoDto getDeletedItems(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long containerId = json.get("item").longValue();
            String sortNotes = json.get("sortNotes").textValue();
            String sortPwd = json.get("sortPwd").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                ContainerDto container = containerService.getDeletedItems(decryptedToken, containerId, sortNotes, sortPwd);
                return cryptoProvider.encrypt(publicKeyPEM, container.toJson());
            }
        }
        return null;
    }

    @PostMapping("/restoreNote")
    public CryptoDto restoreNote(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long noteId = json.get("noteId").longValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.restoreNote(decryptedToken, noteId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/restorePasswd")
    public CryptoDto restorePasswd(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            long pwdId = json.get("pwdId").longValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                MessageDto message = containerService.restorePasswd(decryptedToken, pwdId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getDeletedContainers")
    public CryptoDto getDeletedContainers(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String publicKeyPEM = json.get("publicKey").textValue();
            String token = json.get("token").textValue();
            String sort = json.get("sort").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null) {
                List<ContainerDto> containers = containerService.getDeletedContainers(decryptedToken, sort);
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(containers));
            }
        }
        return null;
    }
}

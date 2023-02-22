package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.UserStatus;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.dto.UserDto;
import com.vladimir.ppm.provider.CryptoProvider;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.UserService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserRestController {
    private final CryptoProvider cryptoProvider;
    private final UserService userService;
    private final ValidatorService validatorService;
    private final TokenService tokenService;
    private final ObjectMapper mapper;

    public UserRestController(CryptoProvider cryptoProvider, UserService userService, ValidatorService validatorService,
                              TokenService tokenService, ObjectMapper mapper) {
        this.cryptoProvider = cryptoProvider;
        this.userService = userService;
        this.validatorService = validatorService;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public CryptoDto login(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String login = json.get("login").textValue();
            String password = json.get("password").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            TokenDto tokenDto = userService.login(login, password, request.getRemoteAddr(), request.getHeader("User-Agent"));
            return cryptoProvider.encrypt(publicKeyPEM, tokenDto.toJson());
        }
        return null;
    }

    @PostMapping("/renewToken")
    public CryptoDto renewToken(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                TokenDto tokenDto = userService.renewToken(decryptedToken);
                return cryptoProvider.encrypt(publicKeyPEM, tokenDto.toJson());
            }
        }
        return null;
    }

    @PostMapping("/setPwd")
    public CryptoDto setPwd(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String newPwd = json.get("pwd").asText();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"), true);
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = userService.changePassword(decryptedToken, newPwd);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getUsers")
    public CryptoDto getUsers(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String sort = json.get("sort").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                List<UserDto> users = userService.getUsers(decryptedToken, sort);
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(users));
            }
        }
        return null;
    }

    @PostMapping("/getStatuses")
    public CryptoDto getStatuses(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken) && userService.isAdmin(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(UserStatus.values()));
            }
        }
        return null;
    }

    @PostMapping("/addUser")
    public CryptoDto addUser(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String login = json.get("login").textValue();
            String pwd = json.get("pwd").textValue();
            boolean changePwd = json.get("changePwd").asBoolean();
            UserStatus status = UserStatus.valueOf(json.get("status").textValue());
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = userService.addUser(decryptedToken, login, pwd, status, changePwd);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/editUser")
    public CryptoDto editUser(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long userId = json.get("id").asLong();
            String login = json.get("login").textValue();
            String pwd = json.get("pwd").textValue();
            boolean changePwd = json.get("changePwd").asBoolean();
            UserStatus status = UserStatus.valueOf(json.get("status").textValue());
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = userService.editUser(decryptedToken, userId, login, pwd, status, changePwd);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/deleteUser")
    public CryptoDto deleteUser(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long userId = json.get("id").asLong();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = userService.deleteUser(decryptedToken, userId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/addAllowedIp")
    public CryptoDto addAllowedIp(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long userId = json.get("userId").asLong();
            String ip = json.get("ip").asText();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = userService.addAllowedIp(decryptedToken, userId, ip);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/getAllowedIp")
    public CryptoDto getAllowedIp(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long userId = json.get("userId").asLong();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(userService.getAllowedIp(decryptedToken, userId)));
            }
        }
        return null;
    }

    @PostMapping("/removeAllowedIp")
    public CryptoDto removeAllowedIp(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long userId = json.get("userId").asLong();
            String ip = json.get("ip").asText();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                userService.removeAllowedIp(decryptedToken, userId, ip);
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(""));
            }
        }
        return null;
    }

    @PostMapping("/getPwdGenSettings")
    public CryptoDto getPwdGenSettings(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(userService.getPwdGenSettings(decryptedToken)));
            }
        }
        return null;
    }

    @PostMapping("/setPwdGenSettings")
    public CryptoDto setPwdGenSettings(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            int pwdLength = json.get("pwdLength").asInt();
            boolean numbers = json.get("numbers").asBoolean();
            boolean symbols = json.get("symbols").asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = userService.setPwdGenSettings(decryptedToken, pwdLength, numbers, symbols);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }
}

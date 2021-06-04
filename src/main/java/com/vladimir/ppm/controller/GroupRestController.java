package com.vladimir.ppm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.GroupDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.provider.CryptoProvider;
import com.vladimir.ppm.service.GroupService;
import com.vladimir.ppm.service.TokenService;
import com.vladimir.ppm.service.UserService;
import com.vladimir.ppm.service.ValidatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupRestController {
    private final CryptoProvider cryptoProvider;
    private final UserService userService;
    private final ValidatorService validatorService;
    private final TokenService tokenService;
    private final GroupService groupService;
    private final ObjectMapper mapper;

    public GroupRestController(CryptoProvider cryptoProvider, UserService userService, ValidatorService validatorService,
                               TokenService tokenService, GroupService groupService, ObjectMapper mapper) {
        this.cryptoProvider = cryptoProvider;
        this.userService = userService;
        this.validatorService = validatorService;
        this.tokenService = tokenService;
        this.groupService = groupService;
        this.mapper = mapper;
    }

    @PostMapping("/getGroups")
    public CryptoDto getGroups(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String sort = json.get("sort").textValue();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                List<GroupDto> groups = groupService.getGroups(decryptedToken, sort);
                return cryptoProvider.encrypt(publicKeyPEM, mapper.writeValueAsString(groups));
            }
        }
        return null;
    }

    @PostMapping("/addGroup")
    public CryptoDto addGroup(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            String name = json.get("name").textValue();
            boolean adminSettings = json.get("admin").asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = groupService.addGroup(decryptedToken, name, adminSettings);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/editGroup")
    public CryptoDto editGroup(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long groupId =  json.get("id").asLong();
            String name = json.get("name").textValue();
            boolean adminSettings = json.get("admin").asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = groupService.editGroup(decryptedToken, groupId, name, adminSettings);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/deleteGroup")
    public CryptoDto deleteGroup(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long groupId = json.get("id").asLong();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = groupService.deleteGroup(decryptedToken, groupId);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }

    @PostMapping("/editGroupMembers")
    public CryptoDto editGroupMembers(@RequestParam String key, @RequestParam String data, HttpServletRequest request) throws JsonProcessingException {
        if (validatorService.validateCrypto(key, data)) {
            JsonNode json = mapper.readTree(cryptoProvider.decrypt(key, data));
            String token = json.get("token").textValue();
            String publicKeyPEM = json.get("publicKey").textValue();
            long groupId = json.get("groupId").asLong();
            long userId = json.get("userId").asLong();
            boolean member = json.get("member").asBoolean();
            Token decryptedToken = tokenService.validateToken(token, request.getRemoteAddr(), request.getHeader("User-Agent"));
            if (decryptedToken != null && userService.isUserEnabled(decryptedToken)) {
                MessageDto message = groupService.editGroupMembers(decryptedToken, groupId, userId, member);
                return cryptoProvider.encrypt(publicKeyPEM, message.toJson());
            }
        }
        return null;
    }
}

package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final TokenService tokenService;
    private final CryptoProvider cryptoProvider;
    private final ValidatorService validatorService;
    private final SettingsProvider settingsProvider;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, TokenService tokenService,
                           CryptoProvider cryptoProvider, ValidatorService validatorService, SettingsProvider settingsProvider) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.cryptoProvider = cryptoProvider;
        this.validatorService = validatorService;
        this.settingsProvider = settingsProvider;
    }

    @Override
    @Transactional
    public TokenDto login(String login, String password, String remoteAddr, String userAgent) {
        User user = userRepository.findUserByLogin(login);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return TokenDto.builder().message("lfe1").build();
        }
        if (cryptoProvider.isSystemClosed() && !isAdmin(user.getGroups())) {
            return TokenDto.builder().message("lfe2").build();
        }
        Token token = tokenService.getToken(user, remoteAddr, userAgent);
        long tokenLifeTime = token.getLifeTime();
        String encryptedToken = tokenService.encrypt(token);
        return TokenDto.builder()
                .lifeTime(tokenLifeTime)
                .token(encryptedToken)
                .adminSettings(isAdmin(user.getGroups()))
                .systemClosed(cryptoProvider.isSystemClosed())
                .build();
    }

    @Override
    @Transactional
    public TokenDto renewToken(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        Token newToken = tokenService.getToken(user, token.getRemoteAddr(), token.getUserAgent());
        long tokenLifeTime = newToken.getLifeTime();
        String encryptedToken = tokenService.encrypt(newToken);
        return TokenDto.builder()
                .lifeTime(tokenLifeTime)
                .token(encryptedToken)
                .adminSettings(isAdmin(user.getGroups()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Group> getGroups(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        return user.getGroups();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        return isAdmin(user.getGroups());
    }

    @Override
    @Transactional
    public MessageDto changePassword(Token token, String newPwd) {
        User user = userRepository.findUserByLogin(token.getLogin());
        if (!validatorService.validatePwdLength(newPwd, settingsProvider.getPwdMinLength())) {
            return MessageDto.builder().message("usse2").build();
        }
        if (encoder.matches(newPwd, user.getPassword())) {
            return MessageDto.builder().message("usse1").build();
        }
        if (settingsProvider.getPwdComplexity() && !validatorService.validatePwdComplexity(newPwd)) {
            return MessageDto.builder().message("usse3").build();
        }
        if (settingsProvider.getPwdSpecialChar() && !validatorService.validatePwdSpecialChar(newPwd)) {
            return MessageDto.builder().message("usse4").build();
        }
        if (!validatorService.validatePwdLoginIncluded(newPwd, token.getLogin())) {
            return MessageDto.builder().message("usse6").build();
        }
        if (!validatorService.validatePwdRepeatedChars(newPwd)) {
            return MessageDto.builder().message("usse7").build();
        }
        user.setPassword(encoder.encode(newPwd));
        return MessageDto.builder().build();
    }

    private boolean isAdmin(Set<Group> groups) {
        for (Group group : groups) {
            if (group.isAdminSettings()) {
                return true;
            }
        }
        return false;
    }
}

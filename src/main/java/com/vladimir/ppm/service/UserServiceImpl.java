package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
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
    private final CryptoProviderService cryptoProviderService;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, TokenService tokenService,
                           CryptoProviderService cryptoProviderService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.cryptoProviderService = cryptoProviderService;
    }

    @Override
    @Transactional
    public TokenDto login(String login, String password, String remoteAddr, String userAgent) {
        User user = userRepository.findUserByLogin(login);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return TokenDto.builder().message("lfe1").build();
        }
        if (cryptoProviderService.isSystemClosed() && !isAdmin(user.getGroups())) {
            return TokenDto.builder().message("lfe2").build();
        }
        Token token = tokenService.getToken(user, remoteAddr, userAgent);
        long tokenLifeTime = token.getLifeTime();
        String encryptedToken = tokenService.encrypt(token);
        return TokenDto.builder()
                .lifeTime(tokenLifeTime)
                .token(encryptedToken)
                .adminSettings(isAdmin(user.getGroups()))
                .systemClosed(cryptoProviderService.isSystemClosed())
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
    @Transactional
    public Set<Group> getGroups(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        return user.getGroups();
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

package com.vladimir.ppm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.provider.CryptoProvider;
import com.vladimir.ppm.provider.SecurityProvider;
import com.vladimir.ppm.provider.SettingsProvider;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final CryptoProvider cryptoProvider;
    private final SettingsProvider settingsProvider;
    private final SecurityProvider securityProvider;

    public TokenServiceImpl(CryptoProvider cryptoProvider, SettingsProvider settingsProvider, SecurityProvider securityProvider) {
        this.cryptoProvider = cryptoProvider;
        this.settingsProvider = settingsProvider;
        this.securityProvider = securityProvider;
    }

    @Override
    public Token getToken(User user, String remoteAddr, String userAgent) {
        long tokenLifeTime = System.currentTimeMillis() + (long) settingsProvider.getTokenLifeTimeMinutes() * 60 * 1000;
        return new Token(user.getLogin(), tokenLifeTime, remoteAddr, userAgent);
    }

    @Override
    public String encrypt(Token token) {
        return cryptoProvider.encryptToken(token);
    }

    @Override
    public Token validateToken(String token, String remoteAddr, String userAgent) throws JsonProcessingException {
        Token decryptedToken = cryptoProvider.decryptToken(token);
        if (decryptedToken.getLifeTime() > System.currentTimeMillis() && decryptedToken.getRemoteAddr().equals(remoteAddr) &&
                decryptedToken.getUserAgent().equals(userAgent)) {
            return decryptedToken;
        }
        securityProvider.registerLoginAttempt(remoteAddr, false);
        return null;
    }
}

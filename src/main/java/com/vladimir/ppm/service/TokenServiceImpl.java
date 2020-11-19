package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Value("${tokenLifeTimeMinutes}")
    private int tokenLifeTimeMinutes;
    private CryptoProviderService cryptoProviderService;

    public TokenServiceImpl(CryptoProviderService cryptoProviderService) {
        this.cryptoProviderService = cryptoProviderService;
    }

    @Override
    public Token getToken(User user, String remoteAddr, String userAgent) {
        long tokenLifeTime = System.currentTimeMillis() + tokenLifeTimeMinutes * 60 * 1000;
        return new Token(user.getLogin(), tokenLifeTime, remoteAddr, userAgent);
    }

    @Override
    public String encrypt(Token token) {
        return cryptoProviderService.encryptToken(token);
    }

    @Override
    public Token validateToken(String token, String remoteAddr, String userAgent) {
        Token decryptedToken = cryptoProviderService.decryptToken(token);
        if (decryptedToken.getLifeTime() < System.currentTimeMillis() && decryptedToken.getRemoteAddr().equals(remoteAddr) &&
                decryptedToken.getUserAgent().equals(userAgent)) {
            return decryptedToken;
        }
        return null;
    }
}

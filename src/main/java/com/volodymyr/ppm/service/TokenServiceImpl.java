package com.volodymyr.ppm.service;

import org.springframework.stereotype.Service;

import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.domain.User;
import com.volodymyr.ppm.provider.CryptoProvider;
import com.volodymyr.ppm.provider.SecurityProvider;
import com.volodymyr.ppm.provider.SettingsProvider;

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
    public Token getToken(User user, String remoteAddr, String userAgent, boolean changePwd) {
        long tokenLifeTime = System.currentTimeMillis() + (long) settingsProvider.getTokenLifeTimeMinutes() * 60 * 1000;
        return new Token(user.getLogin(), tokenLifeTime, remoteAddr, userAgent, changePwd);
    }

    @Override
    public String encrypt(Token token) {
        return cryptoProvider.encryptToken(token);
    }

    @Override
    public Token validateToken(String token, String remoteAddr, String userAgent) {
        return validate(token, remoteAddr, userAgent, false);
    }
    
    @Override
	public Token validateToken(String token, String remoteAddr, String userAgent, boolean changePwd) {
    	return validate(token, remoteAddr, userAgent, changePwd);
	}

	private Token validate(String token, String remoteAddr, String userAgent, boolean changePwd) {
    	Token decryptedToken = cryptoProvider.decryptToken(token);
        if ((changePwd || !decryptedToken.isChangePwd()) && decryptedToken.getLifeTime() > System.currentTimeMillis() && decryptedToken.getRemoteAddr().equals(remoteAddr) &&
                decryptedToken.getUserAgent().equals(userAgent)) {
            return decryptedToken;
        }
        if (!decryptedToken.isChangePwd()) {
        	securityProvider.registerLoginAttempt(remoteAddr, false);
        }
        return null;
    }

}

package com.volodymyr.ppm.service;

import org.springframework.stereotype.Service;

import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.domain.User;
import com.volodymyr.ppm.domain.UserTfaStatus;
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
    public Token getToken(User user, String remoteAddr, String userAgent, String sessionId, boolean changePwd, boolean tfaApproved, boolean tfaSetup) {
    	long tokenLifeTime;
    	if (tfaApproved) {
    		tokenLifeTime = System.currentTimeMillis() + (long) settingsProvider.getTfaTokenLifeTimeMinutes() * 60 * 1000;
    	} else {
    		tokenLifeTime = System.currentTimeMillis() + (long) settingsProvider.getTokenLifeTimeMinutes() * 60 * 1000;    		
    	}
        return new Token(user.getLogin(), tokenLifeTime, remoteAddr, userAgent, changePwd, sessionId, tfaApproved, tfaSetup);
    }

    @Override
    public String encrypt(Token token) {
        return cryptoProvider.encryptToken(token);
    }
    
    @Override
    public Token decryptToken(String token) {
    	return cryptoProvider.decryptToken(token);
    }

    @Override
    public Token validateToken(User user, String token, String remoteAddr, String userAgent, String sessionId) {
        return validate(user, token, remoteAddr, userAgent, sessionId, false, false);
    }
    
    @Override
	public Token validateToken(User user, String token, String remoteAddr, String userAgent, String sessionId, boolean changePwd, boolean validateTfaCode) {
    	return validate(user, token, remoteAddr, userAgent, sessionId, changePwd, validateTfaCode);
	}

	private Token validate(User user, String token, String remoteAddr, String userAgent, String sessionId, boolean changePwd, boolean validateTfaCode) {
    	Token decryptedToken = cryptoProvider.decryptToken(token);
    	boolean isTokenSourceValid = decryptedToken.getLifeTime() > System.currentTimeMillis() && decryptedToken.getRemoteAddr().equals(remoteAddr) && decryptedToken.getUserAgent().equals(userAgent) && decryptedToken.getSessionId().equals(sessionId);
    	boolean isTokenTfaStatusValid = user.getTfaStatus() == UserTfaStatus.DISABLED || (user.getTfaStatus() == UserTfaStatus.INPROGRESS && !decryptedToken.isTfaSetup()) || (user.getTfaStatus() == UserTfaStatus.CONFIGURED && decryptedToken.isTfaApproved());
        if (isTokenSourceValid && (isTokenTfaStatusValid || validateTfaCode) && (changePwd || !decryptedToken.isChangePwd())){
            return decryptedToken;
        }
        if (!decryptedToken.isChangePwd()) {
        	securityProvider.registerLoginAttempt(remoteAddr, false);
        }
        return null;
    }

}

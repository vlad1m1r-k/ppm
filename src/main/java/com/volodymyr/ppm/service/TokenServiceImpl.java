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
    public Token getToken(User user, String remoteAddr, String userAgent, boolean changePwd, boolean tfaApproved) {
    	long tokenLifeTime;
    	if (tfaApproved) {
    		tokenLifeTime = System.currentTimeMillis() + (long) settingsProvider.getTfaTokenLifeTimeMinutes() * 60 * 1000;
    	} else {
    		tokenLifeTime = System.currentTimeMillis() + (long) settingsProvider.getTokenLifeTimeMinutes() * 60 * 1000;    		
    	}
        return new Token(user.getLogin(), tokenLifeTime, remoteAddr, userAgent, changePwd, "", tfaApproved);
        //TODO implement sessionId check
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
    public Token validateToken(User user, String token, String remoteAddr, String userAgent) {
        return validate(user, token, remoteAddr, userAgent, false, false);
    }
    
    @Override
	public Token validateToken(User user, String token, String remoteAddr, String userAgent, boolean changePwd, boolean validateTfaCode) {
    	return validate(user, token, remoteAddr, userAgent, changePwd, validateTfaCode);
	}

	private Token validate(User user, String token, String remoteAddr, String userAgent, boolean changePwd, boolean validateTfaCode) {
    	Token decryptedToken = cryptoProvider.decryptToken(token);
    	boolean isTokenSourceValid = decryptedToken.getLifeTime() > System.currentTimeMillis() && decryptedToken.getRemoteAddr().equals(remoteAddr) && decryptedToken.getUserAgent().equals(userAgent);
    	boolean isTokenTfaStatusValid = !user.isTfaEnabled() || decryptedToken.isTfaApproved() || validateTfaCode;
        if (isTokenSourceValid && isTokenTfaStatusValid && (changePwd || !decryptedToken.isChangePwd())){
        	//TODO implement sessionId check
            return decryptedToken;
        }
        if (!decryptedToken.isChangePwd()) {
        	securityProvider.registerLoginAttempt(remoteAddr, false);
        }
        return null;
    }

}

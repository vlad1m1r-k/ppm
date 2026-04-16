package com.volodymyr.ppm.domain;

import tools.jackson.databind.ObjectMapper;

public class Token {
    private final String login;
    private final long lifeTime;
    private final String remoteAddr;
    private final String userAgent;
    private final String sessionId;
    private final boolean changePwd;
    private final boolean tfaApproved;
    private final boolean tfaSetup;

    public Token(String login, long lifeTime, String remoteAddr, String userAgent, boolean changePwd, String sessionId, boolean tfaApproved, boolean tfaSetup) {
        this.login = login;
        this.lifeTime = lifeTime;
        this.remoteAddr = remoteAddr;
        this.userAgent = userAgent;
		this.changePwd = changePwd;
		this.sessionId = sessionId;
		this.tfaApproved = tfaApproved;
		this.tfaSetup = tfaSetup;
    }

    public String getLogin() {
        return login;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getUserAgent() {
        return userAgent;
    }
    
    public String getSessionId() {
    	return sessionId;
    }
    
    public boolean isChangePwd() {
    	return changePwd;
    }
    
    public boolean isTfaApproved() {
    	return tfaApproved;
    }
    
    public boolean isTfaSetup() {
    	return tfaSetup;
    }

    public String toJson() {
        return new ObjectMapper().writeValueAsString(this);
    }
}

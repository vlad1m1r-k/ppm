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

    public Token(String login, long lifeTime, String remoteAddr, String userAgent, boolean changePwd, String sessionId) {
        this.login = login;
        this.lifeTime = lifeTime;
        this.remoteAddr = remoteAddr;
        this.userAgent = userAgent;
		this.changePwd = changePwd;
		this.sessionId = sessionId;
		this.tfaApproved = false;
    }
    
    public Token(String login, long lifeTime, String remoteAddr, String userAgent, boolean changePwd, String sessionId, boolean tfaApproved) {
        this.login = login;
        this.lifeTime = lifeTime;
        this.remoteAddr = remoteAddr;
        this.userAgent = userAgent;
		this.changePwd = changePwd;
		this.sessionId = sessionId;
		this.tfaApproved = tfaApproved;
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

    public String toJson() {
        return new ObjectMapper().writeValueAsString(this);
    }
}

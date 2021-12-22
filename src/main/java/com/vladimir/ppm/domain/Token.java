package com.vladimir.ppm.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Token {
    private final String login;
    private final long lifeTime;
    private final String remoteAddr;
    private final String userAgent;
    private final boolean changePwd;

    public Token(String login, long lifeTime, String remoteAddr, String userAgent, boolean changePwd) {
        this.login = login;
        this.lifeTime = lifeTime;
        this.remoteAddr = remoteAddr;
        this.userAgent = userAgent;
		this.changePwd = changePwd;
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
    
    public boolean isChangePwd() {
    	return changePwd;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}

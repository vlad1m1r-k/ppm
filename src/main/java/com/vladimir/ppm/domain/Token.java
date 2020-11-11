package com.vladimir.ppm.domain;

import org.json.JSONObject;

public class Token {
    private final String login;
    private final long lifeTime;
    private final String remoteAddr;
    private final String userAgent;

    public Token(String login, long lifeTime, String remoteAddr, String userAgent) {
        this.login = login;
        this.lifeTime = lifeTime;
        this.remoteAddr = remoteAddr;
        this.userAgent = userAgent;
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

    public String toJson() {
        return new JSONObject(this).toString();
    }
}

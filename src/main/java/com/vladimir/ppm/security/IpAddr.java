package com.vladimir.ppm.security;

public class IpAddr {
    private final String ip;
    private long lastLoginAttemptTime;
    private int totalLoginAttempts;
    private boolean banned;
    private long unbanTime;

    public IpAddr(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public long getLastLoginAttemptTime() {
        return lastLoginAttemptTime;
    }

    public int getTotalLoginAttempts() {
        return totalLoginAttempts;
    }

    public boolean isBanned() {
        return banned;
    }

    public long getUnbanTime() {
        return unbanTime;
    }

    public void setLastLoginAttemptTime(long lastLoginAttemptTime) {
        this.lastLoginAttemptTime = lastLoginAttemptTime;
    }

    public void setTotalLoginAttempts(int totalLoginAttempts) {
        this.totalLoginAttempts = totalLoginAttempts;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setUnbanTime(long unbanTime) {
        this.unbanTime = unbanTime;
    }
}

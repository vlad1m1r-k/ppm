package com.vladimir.ppm.service;

import java.util.Set;

public interface SettingsProvider {
    int getServerKeyLifeTimeDays();
    int getTokenLifeTimeMinutes();
    Long getDBEncryptionKeyId();
    int getPwdMinLength();
    boolean getPwdComplexity();
    boolean getPwdSpecialChar();
    int getIncorrectLoginAttempts();
    int getIpBanTimeDays();
    int getIncorrectPasswdAttempts();
    Set<String> getIpBlacklist();
    Set<String> getIpWhiteList();
    void setServerKeyLifeTimeDays(int lifeTime);
    void setTokenLifeTimeMinutes(int lifeTime);
    void setDBEncryptionKeyId(long id);
    void setPwdMinLength(int minLength);
    void setPwdComplexity(boolean complexity);
    void setPwdSpecialChar(boolean specialChar);
    void setIncorrectLoginAttempts(int attempts);
    void setIpBanTimeDays(int days);
    void setIncorrectPasswdAttempts(int attempts);
    void addIpToBlackList(String ip);
    void addIpToWhiteList(String ip);
}

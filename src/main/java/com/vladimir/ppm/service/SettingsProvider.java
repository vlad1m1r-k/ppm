package com.vladimir.ppm.service;

public interface SettingsProvider {
    int getServerKeyLifeTimeDays();
    int getTokenLifeTimeMinutes();
    Long getDBEncryptionKeyId();
    int getPwdMinLength();
    boolean getPwdComplexity();
    boolean getPwdSpecialChar();
    void setServerKeyLifeTimeDays(int lifeTime);
    void setTokenLifeTimeMinutes(int lifeTime);
    void setDBEncryptionKeyId(long id);
    void setPwdMinLength(int minLength);
    void setPwdComplexity(boolean complexity);
    void setPwdSpecialChar(boolean specialChar);
}

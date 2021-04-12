package com.vladimir.ppm.service;

public interface SettingsProvider {
    int getServerKeyLifeTimeDays();
    void setServerKeyLifeTimeDays(int lifeTime);
    int getTokenLifeTimeMinutes();
    void setTokenLifeTimeMinutes(int lifeTime);
    Long getDBEncryptionKeyId();
    void setDBEncryptionKeyId(long id);
}

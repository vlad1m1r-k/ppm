package com.vladimir.ppm.service;

public interface SettingsService {
    int getServerKeyLifeTimeDays();
    boolean setServerKeyLifeTimeDays(int lifeTime);
    int getTokenLifeTimeMinutes();
    boolean setTokenLifeTimeMinutes(int lifeTime);
    Long getDBEncryptionKeyId();
    void setDBEncryptionKeyId(long id);
}

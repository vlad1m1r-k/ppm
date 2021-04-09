package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;

import java.io.IOException;

public interface SettingsService {
    MessageDto getDbStatus(Token token);
    MessageDto generateDbKey(Token token);
    MessageDto installDbKey(Token token, String key) throws IOException;
    int getServerKeyLifeTimeDays();
    boolean setServerKeyLifeTimeDays(int lifeTime);
    int getTokenLifeTimeMinutes();
    boolean setTokenLifeTimeMinutes(int lifeTime);
    long getDBEncryptionKeyId();
    boolean setDBEncryptionKeyId(long id);
}

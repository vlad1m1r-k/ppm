package com.vladimir.ppm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;

import java.io.IOException;

public interface SettingsService {
    MessageDto getDbStatus(Token token);
    MessageDto generateDbKey(Token token);
    MessageDto installDbKey(Token token, String key) throws IOException;
}

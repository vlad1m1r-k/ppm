package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;

public interface SettingsService {
    MessageDto getDbStatus(Token token);
}

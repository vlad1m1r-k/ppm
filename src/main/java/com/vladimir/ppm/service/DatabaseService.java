package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;

import java.io.IOException;

public interface DatabaseService {
    MessageDto getDBStatus(Token token);
    MessageDto generateDbKey(Token token);
    MessageDto installDbKey(Token token, String key) throws IOException;
}

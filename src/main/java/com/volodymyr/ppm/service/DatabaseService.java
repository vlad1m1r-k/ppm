package com.volodymyr.ppm.service;

import java.io.IOException;

import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.dto.MessageDto;

public interface DatabaseService {
    MessageDto getDBStatus(Token token);
    MessageDto generateDbKey(Token token);
    MessageDto installDbKey(Token token, String key) throws IOException;
}

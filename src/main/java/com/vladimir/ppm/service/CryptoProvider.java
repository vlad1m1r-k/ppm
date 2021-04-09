package com.vladimir.ppm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.DbStatus;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.PublicKeyDto;

import java.io.IOException;

public interface CryptoProvider {
    PublicKeyDto getPublicKey();
    CryptoDto encrypt(String publicKey, String data);
    String decrypt(String key, String data);
    String encryptToken(Token token);
    Token decryptToken(String token) throws JsonProcessingException;
    boolean isSystemClosed();
    MessageDto getDBStatus(Token token);
    MessageDto generateDbKey(Token token);
    MessageDto installDbKey(Token token, String key) throws IOException;
    byte[] encryptDbEntry(String text);
    String decryptDbEntry(byte[] bytes);
}

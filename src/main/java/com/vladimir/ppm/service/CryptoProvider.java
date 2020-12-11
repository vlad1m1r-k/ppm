package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.PublicKeyDto;

public interface CryptoProvider {
    PublicKeyDto getPublicKey();
    CryptoDto encrypt(String publicKey, String data);
    String decrypt(String key, String data);
    String encryptToken(Token token);
    Token decryptToken(String token);
    boolean isSystemClosed();
    DbKey generateDbKey();
    void installDbKey(byte[] key);
}

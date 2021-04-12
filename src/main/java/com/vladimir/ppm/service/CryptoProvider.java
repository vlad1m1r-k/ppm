package com.vladimir.ppm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.PublicKeyDto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface CryptoProvider {
    PublicKeyDto getPublicKey();
    CryptoDto encrypt(String publicKey, String data);
    String decrypt(String key, String data);
    String encryptToken(Token token);
    Token decryptToken(String token) throws JsonProcessingException;
    boolean isSystemClosed();
    DbKey generateDbKey();
    void installDbKey(byte[] dbKey);
    byte[] encryptDbEntry(String text);
    String decryptDbEntry(byte[] bytes);
    void generateServerKeypair() throws NoSuchAlgorithmException, NoSuchProviderException;
}

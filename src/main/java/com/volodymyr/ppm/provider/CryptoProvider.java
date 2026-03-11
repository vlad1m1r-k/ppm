package com.volodymyr.ppm.provider;

import com.volodymyr.ppm.domain.DbKey;
import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.dto.CryptoDto;
import com.volodymyr.ppm.dto.PublicKeyDto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface CryptoProvider {
    PublicKeyDto getPublicKey();
    CryptoDto encrypt(String publicKey, String data);
    String decrypt(String key, String data);
    String encryptToken(Token token);
    Token decryptToken(String token);
    boolean isSystemClosed();
    DbKey generateDbKey();
    void installDbKey(byte[] dbKey);
    byte[] encryptDbEntry(String text);
    String decryptDbEntry(byte[] bytes);
    void generateServerKeypair() throws NoSuchAlgorithmException, NoSuchProviderException;
}

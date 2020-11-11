package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.PublicKeyDto;

public interface CryptoProviderService {
    PublicKeyDto getPublicKey();
    CryptoDto encrypt(String publicKey, String data);
    String decrypt(String key, String data);
    String encryptToken(Token token);
}

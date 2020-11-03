package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.PublicKeyDto;

public interface CryptoProviderService {
    PublicKeyDto getPublicKey();
    String decrypt(String key, String data);
}

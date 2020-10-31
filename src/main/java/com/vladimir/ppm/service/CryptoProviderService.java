package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.PublicKeyDto;

public interface CryptoProviderService {
    PublicKeyDto getPublicKey();
    void test(String key, String data);
}

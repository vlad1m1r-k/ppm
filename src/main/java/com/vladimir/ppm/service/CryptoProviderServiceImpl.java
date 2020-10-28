package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.PublicKeyDto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

@Service
public class CryptoProviderServiceImpl implements CryptoProviderService {
    private KeyPair keyPair;
    private Long keyPairExpireDate;

    @Value("${keyLifeTimeDays}")
    private int keyLifeTimeDays;

    @PostConstruct
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            SecureRandom random = new SecureRandom();
            keyPairGenerator.initialize(2048, random);
            keyPair = keyPairGenerator.generateKeyPair();
            keyPairExpireDate = System.currentTimeMillis() + keyLifeTimeDays * 24 * 60 * 60 * 1000;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PublicKeyDto getPublicKey() {
        return PublicKeyDto.builder()
                .keyPairExpireDate(keyPairExpireDate)
                .publicKey(Base64.getEncoder().withoutPadding().encodeToString(keyPair.getPublic().getEncoded()))
                .build();
    }
}

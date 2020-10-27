package com.vladimir.ppm.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

@Service
public class CryptoProviderServiceImpl implements CryptoProviderService {
    private KeyPair keyPair;
    private BASE64Encoder b64Encoder = new BASE64Encoder();

    @PostConstruct
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            SecureRandom random = new SecureRandom();
            keyPairGenerator.initialize(2048, random);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPublicKey() {
        return b64Encoder.encode(keyPair.getPublic().getEncoded());
    }
}

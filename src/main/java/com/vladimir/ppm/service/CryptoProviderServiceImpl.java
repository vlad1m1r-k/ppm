package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.PublicKeyDto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
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
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
            return PublicKeyDto.builder()
                    .keyPairExpireDate(keyPairExpireDate)
                    .modulus(Base64.getEncoder().withoutPadding().encodeToString(keySpec.getModulus().toByteArray()))
                    .exponent(Base64.getEncoder().withoutPadding().encodeToString(keySpec.getPublicExponent().toByteArray()))
                    .build();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void test(String key, String data) {
        System.out.println(key.length());
        byte[] keyBytes = Base64.getDecoder().decode(key);
        System.out.println(keyBytes.length);
        System.out.println(new String(keyBytes));
        byte[] dataBytes = Base64.getDecoder().decode(data);
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            System.out.println(new String(rsaCipher.doFinal(keyBytes)));

            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(null));
//            System.out.println(new String(aesCipher.doFinal(dataBytes)));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}

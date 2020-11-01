package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.PublicKeyDto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
        String privateKeyPem = "";
        try (StringWriter stringWriter = new StringWriter(); JcaPEMWriter keyWriter = new JcaPEMWriter(stringWriter)) {
            keyWriter.writeObject(keyPair.getPublic());
            keyWriter.flush();
            stringWriter.flush();
            privateKeyPem = stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return PublicKeyDto.builder()
                .keyPairExpireDate(keyPairExpireDate)
                .keyPEM(privateKeyPem)
                .build();
    }

    @Override
    public void test(String key, String data) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        byte[] dataBytes = Base64.getDecoder().decode(data);
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            JSONObject json = new JSONObject(new String(rsaCipher.doFinal(keyBytes)));
            byte[] aesKeyBytes = Base64.getDecoder().decode(json.getString("key"));
            byte[] aesIVBytes = Base64.getDecoder().decode(json.getString("iv"));
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKeyBytes, "AES"), new IvParameterSpec(aesIVBytes));
            System.out.println(new String(aesCipher.doFinal(dataBytes)));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
}

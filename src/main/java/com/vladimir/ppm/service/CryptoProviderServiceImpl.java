package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class CryptoProviderServiceImpl implements CryptoProviderService {
    private KeyPair keyPair;
    private Long keyPairExpireDate;
    private SecretKeySpec tokenAESKey;
    private Cipher rsaCipher;
    private Cipher aesCipher;
    SecureRandom random = new SecureRandom();

    @Value("${keyLifeTimeDays}")
    private int keyLifeTimeDays;

    @PostConstruct
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(2048, random);
            keyPair = keyPairGenerator.generateKeyPair();
            keyPairExpireDate = System.currentTimeMillis() + keyLifeTimeDays * 24 * 60 * 60 * 1000;

            byte[] aesKeyBytes = new byte[32];
            random.nextBytes(aesKeyBytes);
            tokenAESKey = new SecretKeySpec(aesKeyBytes, "AES");

            rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            aesCipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PublicKeyDto getPublicKey() {
        String publicKeyPem = "";
        try (StringWriter stringWriter = new StringWriter(); JcaPEMWriter keyWriter = new JcaPEMWriter(stringWriter)) {
            keyWriter.writeObject(keyPair.getPublic());
            keyWriter.flush();
            stringWriter.flush();
            publicKeyPem = stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return PublicKeyDto.builder()
                .keyPairExpireDate(keyPairExpireDate)
                .keyPEM(publicKeyPem)
                .build();
    }

    @Override
    public CryptoDto encrypt(String publicKey, String data) {
        byte[] aesKey = new byte[32];
        byte[] aesIv = new byte[16];
        random.nextBytes(aesKey);
        random.nextBytes(aesIv);
        byte[] encryptedData = null;
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(aesIv));
            encryptedData = aesCipher.doFinal(data.getBytes());
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        String aesKeyB64 = Base64.getEncoder().encodeToString(aesKey);
        String aesIvB64 = Base64.getEncoder().encodeToString(aesIv);
        JSONObject aesKeyBundle = new JSONObject();
        aesKeyBundle.put("key", aesKeyB64);
        aesKeyBundle.put("iv", aesIvB64);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey
                .replaceAll("\r\n", "")
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", ""));
        byte[] encryptedAesKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey frontPublicKey = keyFactory.generatePublic(keySpec);
            rsaCipher.init(Cipher.ENCRYPT_MODE, frontPublicKey);
            encryptedAesKey = rsaCipher.doFinal(aesKeyBundle.toString().getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return CryptoDto.builder()
                .key(Base64.getEncoder().encodeToString(encryptedAesKey))
                .data(Base64.getEncoder().encodeToString(encryptedData))
                .build();
    }

    @Override
    public String decrypt(String key, String data) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        byte[] dataBytes = Base64.getDecoder().decode(data);
        String decryptedString = "";
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            JSONObject json = new JSONObject(new String(rsaCipher.doFinal(keyBytes)));
            byte[] aesKeyBytes = Base64.getDecoder().decode(json.getString("key"));
            byte[] aesIVBytes = Base64.getDecoder().decode(json.getString("iv"));

            aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKeyBytes, "AES"), new IvParameterSpec(aesIVBytes));
            decryptedString = new String(aesCipher.doFinal(dataBytes));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }

    @Override
    public String encryptToken(Token token) {
        String encryptedB64Token = null;
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, tokenAESKey);
            byte[] encryptedToken = aesCipher.doFinal(token.toJson().getBytes());
            encryptedB64Token = Base64.getEncoder().encodeToString(encryptedToken);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return encryptedB64Token;
    }

    @Override
    public Token decryptToken(String token) {
        String tokenStr = "";
        try {
            aesCipher.init(Cipher.DECRYPT_MODE, tokenAESKey);
            tokenStr = new String(aesCipher.doFinal(Base64.getDecoder().decode(token)));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(tokenStr);
        return new Token(json.getString("login"), json.getLong("lifeTime"), json.getString("remoteAddr"), json.getString("userAgent"));
    }
}

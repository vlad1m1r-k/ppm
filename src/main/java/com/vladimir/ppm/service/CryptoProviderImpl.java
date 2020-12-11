package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.PublicKeyDto;
import org.apache.commons.lang3.ArrayUtils;
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
import java.nio.charset.StandardCharsets;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class CryptoProviderImpl implements CryptoProvider {
    private KeyPair keyPair;
    private Long keyPairExpireDate; //TODO renew keypair
    private SecretKeySpec tokenAESKey;
    private SecretKeySpec dbAESKey;
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
            keyPairExpireDate = System.currentTimeMillis() + (long) keyLifeTimeDays * 24 * 60 * 60 * 1000;

            byte[] aesKey = new byte[32];
            random.nextBytes(aesKey);
            tokenAESKey = new SecretKeySpec(aesKey, "AES");

            //TODO remove it
            byte[] dbKey = {25, 126, -91, 78, 87, 110, -25, -27, 6, 121, 44, 96, -63, 17, 32, -69, -29, -8, 51, -12, 80, -44, 61, 108, 120, 36, -55, -86, 2, -117, -119, -123};
            dbAESKey = new SecretKeySpec(dbKey, "AES");

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
        byte[] iv = generateIV();
        IvParameterSpec aesIv = new IvParameterSpec(iv);
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, tokenAESKey, aesIv);
            byte[] encryptedToken = aesCipher.doFinal(token.toJson().getBytes());
            encryptedToken = insertIv(iv, encryptedToken);
            encryptedB64Token = Base64.getEncoder().encodeToString(encryptedToken);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return encryptedB64Token;
    }

    @Override
    public Token decryptToken(String token) {
        String tokenStr = "";
        Map<String, byte[]> tokenMap = extractIv(Base64.getDecoder().decode(token));
        IvParameterSpec aesIv = new IvParameterSpec(tokenMap.get("iv"));
        try {
            aesCipher.init(Cipher.DECRYPT_MODE, tokenAESKey, aesIv);
            tokenStr = new String(aesCipher.doFinal(tokenMap.get("data")));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(tokenStr);
        return new Token(json.getString("login"), json.getLong("lifeTime"), json.getString("remoteAddr"), json.getString("userAgent"));
    }

    @Override
    public boolean isSystemClosed() {
        return dbAESKey == null;
    }

    @Override
    public DbKey generateDbKey() {
        byte[] key = new byte[32];
        random.nextBytes(key);
        return new DbKey(System.currentTimeMillis(), key);
    }

    @Override
    public void installDbKey(byte[] key) {
        dbAESKey = new SecretKeySpec(key, "AES");
    }

    private byte[] generateIV() {
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    private byte[] insertIv(byte[] iv, byte[] data) {
        return ArrayUtils.addAll(iv, data);
    }

    private Map<String, byte[]> extractIv(byte[] data) {
        Map<String, byte[]> result = new HashMap<>();
        byte[] iv = ArrayUtils.subarray(data, 0, 16);
        byte[] body = ArrayUtils.subarray(data, 16, data.length);
        result.put("iv", iv);
        result.put("data", body);
        return result;
    }
}

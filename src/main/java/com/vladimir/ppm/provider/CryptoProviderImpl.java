package com.vladimir.ppm.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Acts;
import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.Objects;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.PublicKeyDto;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@EnableScheduling
public class CryptoProviderImpl implements CryptoProvider {
    private final SettingsProvider settingsProvider;
    private final Logger logger;
    private final ObjectMapper mapper;
    private final String RSACIPHER = "RSA/ECB/PKCS1Padding";
    private final String AESCIPHER = "AES/CBC/PKCS7Padding";
    private final String PROVIDER = "BC";
    private KeyPair keyPair;
    private Long keyPairExpireDate;
    private SecretKeySpec tokenAESKey;
    private SecretKeySpec dbAESKey;
    SecureRandom random = new SecureRandom();

    public CryptoProviderImpl(SettingsProvider settingsProvider, Logger logger, ObjectMapper mapper) {
        this.settingsProvider = settingsProvider;
        this.logger = logger;
        this.mapper = mapper;
    }

    @PostConstruct
    private void init() throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        generateServerKeypair();

        byte[] aesKey = new byte[32];
        random.nextBytes(aesKey);
        tokenAESKey = new SecretKeySpec(aesKey, "AES");
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 10 * 60 * 60 * 1000)
    private void renewServerKeypair() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (System.currentTimeMillis() > keyPairExpireDate) {
            generateServerKeypair();
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
            Cipher aesCipher = Cipher.getInstance(AESCIPHER, PROVIDER);
            aesCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(aesIv));
            encryptedData = aesCipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                BadPaddingException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        String aesKeyB64 = Base64.getEncoder().encodeToString(aesKey);
        String aesIvB64 = Base64.getEncoder().encodeToString(aesIv);
        Map<String, String> aesKeyBundle = new HashMap<>();
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
            Cipher rsaCipher = Cipher.getInstance(RSACIPHER, PROVIDER);
            rsaCipher.init(Cipher.ENCRYPT_MODE, frontPublicKey);
            encryptedAesKey = rsaCipher.doFinal(mapper.writeValueAsString(aesKeyBundle).getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException | NoSuchPaddingException | JsonProcessingException e) {
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
            Cipher rsaCipher = Cipher.getInstance(RSACIPHER, PROVIDER);
            rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            JsonNode json = mapper.readTree(new String(rsaCipher.doFinal(keyBytes)));
            byte[] aesKeyBytes = Base64.getDecoder().decode(json.get("key").textValue());
            byte[] aesIVBytes = Base64.getDecoder().decode(json.get("iv").textValue());

            Cipher aesCipher = Cipher.getInstance(AESCIPHER, PROVIDER);
            aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKeyBytes, "AES"), new IvParameterSpec(aesIVBytes));
            decryptedString = new String(aesCipher.doFinal(dataBytes), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
                | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | JsonProcessingException e) {
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
            Cipher aesCipher = Cipher.getInstance(AESCIPHER, PROVIDER);
            aesCipher.init(Cipher.ENCRYPT_MODE, tokenAESKey, aesIv);
            byte[] encryptedToken = aesCipher.doFinal(token.toJson().getBytes(StandardCharsets.UTF_8));
            encryptedToken = insertIv(iv, encryptedToken);
            encryptedB64Token = Base64.getEncoder().encodeToString(encryptedToken);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
                | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return encryptedB64Token;
    }

    @Override
    public Token decryptToken(String tokenStr) throws JsonProcessingException {
    	Token token = null;
        Map<String, byte[]> tokenMap = extractIv(Base64.getDecoder().decode(tokenStr));
        IvParameterSpec aesIv = new IvParameterSpec(tokenMap.get("iv"));
        try {
            Cipher aesCipher = Cipher.getInstance(AESCIPHER, PROVIDER);
            aesCipher.init(Cipher.DECRYPT_MODE, tokenAESKey, aesIv);
            token = mapper.readValue(aesCipher.doFinal(tokenMap.get("data")), Token.class);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    @Override
    public boolean isSystemClosed() {
        return dbAESKey == null;
    }

    @Override
    public DbKey generateDbKey() {
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return new DbKey(System.currentTimeMillis(), keyBytes);
    }

    @Override
    public void installDbKey(byte[] dbKey) {
        dbAESKey = new SecretKeySpec(dbKey, "AES");
    }

    @Override
    public byte[] encryptDbEntry(String text) {
        byte[] iv = generateIV();
        IvParameterSpec aesIv = new IvParameterSpec(iv);
        byte[] encryptedText = new byte[0];
        try {
            Cipher aesCipher = Cipher.getInstance(AESCIPHER, PROVIDER);
            aesCipher.init(Cipher.ENCRYPT_MODE, dbAESKey, aesIv);
            encryptedText = aesCipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            encryptedText = insertIv(iv, encryptedText);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    @Override
    public String decryptDbEntry(byte[] bytes) {
        String text = "";
        Map<String, byte[]> entryMap = extractIv(bytes);
        IvParameterSpec aesIv = new IvParameterSpec(entryMap.get("iv"));
        try {
            Cipher aesCipher = Cipher.getInstance(AESCIPHER, PROVIDER);
            aesCipher.init(Cipher.DECRYPT_MODE, dbAESKey, aesIv);
            text = new String(aesCipher.doFinal(entryMap.get("data")), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    public void generateServerKeypair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", PROVIDER);
        keyPairGenerator.initialize(2048, random);
        keyPair = keyPairGenerator.generateKeyPair();
        keyPairExpireDate = System.currentTimeMillis() + (long) settingsProvider.getServerKeyLifeTimeDays() * 24 * 60 * 60 * 1000;
        logger.log("System", Acts.CREATE, Objects.KEY, "Server Keypair", new Date(), "");
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

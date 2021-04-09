package com.vladimir.ppm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.DbStatus;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.CryptoDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.PublicKeyDto;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.context.annotation.Lazy;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@EnableScheduling
public class CryptoProviderImpl implements CryptoProvider {
    private final SettingsService settingsService;
    private final UserService userService;
    private final String RSACIPHER = "RSA/ECB/PKCS1Padding";
    private final String AESCIPHER = "AES/CBC/PKCS7Padding";
    private final String PROVIDER = "BC";
    private KeyPair keyPair;
    private Long keyPairExpireDate;
    private SecretKeySpec tokenAESKey;
    private SecretKeySpec dbAESKey;
    SecureRandom random = new SecureRandom();

    public CryptoProviderImpl(SettingsService settingsService, @Lazy UserService userService) {
        this.settingsService = settingsService;
        this.userService = userService;
    }

    @PostConstruct
    private void init() throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        generateServerKeypair();

        byte[] aesKey = new byte[32];
        random.nextBytes(aesKey);
        tokenAESKey = new SecretKeySpec(aesKey, "AES");

        //TODO remove it
        byte[] dbKey = {25, 126, -91, 78, 87, 110, -25, -27, 6, 121, 44, 96, -63, 17, 32, -69, -29, -8, 51, -12, 80, -44, 61, 108, 120, 36, -55, -86, 2, -117, -119, -123};
        dbAESKey = new SecretKeySpec(dbKey, "AES");
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
            encryptedAesKey = rsaCipher.doFinal(new ObjectMapper().writeValueAsString(aesKeyBundle).getBytes());
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
            JsonNode json = new ObjectMapper().readTree(new String(rsaCipher.doFinal(keyBytes)));
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
    public Token decryptToken(String token) throws JsonProcessingException {
        String tokenStr = "";
        Map<String, byte[]> tokenMap = extractIv(Base64.getDecoder().decode(token));
        IvParameterSpec aesIv = new IvParameterSpec(tokenMap.get("iv"));
        try {
            Cipher aesCipher = Cipher.getInstance(AESCIPHER, PROVIDER);
            aesCipher.init(Cipher.DECRYPT_MODE, tokenAESKey, aesIv);
            tokenStr = new String(aesCipher.doFinal(tokenMap.get("data")), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        JsonNode json = new ObjectMapper().readTree(tokenStr);
        return new Token(json.get("login").textValue(), json.get("lifeTime").longValue(), json.get("remoteAddr").textValue(), json.get("userAgent").textValue());
    }

    @Override
    public boolean isSystemClosed() {
        return dbAESKey == null;
    }

    @Override
    public MessageDto getDBStatus(Token token) {
        if (userService.isAdmin(token)) {
            return MessageDto.builder()
                    .message(getDBEncryptionStatus().name())
                    .build();
        }
        return null;
    }

    @Override
    public MessageDto generateDbKey(Token token) {
        if (userService.isAdmin(token) && getDBEncryptionStatus() == DbStatus.NEW_DB) {
            byte[] keyBytes = new byte[32];
            random.nextBytes(keyBytes);
            DbKey key = new DbKey(System.currentTimeMillis(), keyBytes);
            settingsService.setDBEncryptionKeyId(key.getId());
            dbAESKey = new SecretKeySpec(key.getKey(), "AES");
            return MessageDto.builder()
                    .message(key.toString())
                    .build();
        }
        return null;
    }

    @Override
    public MessageDto installDbKey(Token token, String key) throws IOException {
        if (userService.isAdmin(token) && getDBEncryptionStatus() == DbStatus.NEED_KEY) {
            JsonNode json = new ObjectMapper().readTree(new String(Base64.getDecoder()
                    .decode(key.replaceAll("\n", "").replaceAll("\r", ""))));
            long id = json.get("id").longValue();
            if (settingsService.getDBEncryptionKeyId() != id) {
                return MessageDto.builder().message("db10").build();
            }
            Byte[] dbKey = StreamSupport.stream(json.get("key").spliterator(), false).map(o -> Byte.valueOf(String.valueOf(o))).toArray(Byte[]::new);
            dbAESKey = new SecretKeySpec(ArrayUtils.toPrimitive(dbKey), "AES");
        }
        return MessageDto.builder().build();
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

    private void generateServerKeypair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", PROVIDER);
        keyPairGenerator.initialize(2048, random);
        keyPair = keyPairGenerator.generateKeyPair();
        keyPairExpireDate = System.currentTimeMillis() + (long) settingsService.getServerKeyLifeTimeDays() * 24 * 60 * 60 * 1000;
    }

    private DbStatus getDBEncryptionStatus() {
        if (isSystemClosed()) {
            return DbStatus.OK;
        }
        if (settingsService.getDBEncryptionKeyId() == null) {
            return DbStatus.NEW_DB;
        }
        return DbStatus.NEED_KEY;
    }
}

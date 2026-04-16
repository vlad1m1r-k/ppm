package com.volodymyr.ppm.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.volodymyr.ppm.domain.DbKey;
import com.volodymyr.ppm.domain.DbStatus;
import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.dto.MessageDto;
import com.volodymyr.ppm.provider.CryptoProvider;
import com.volodymyr.ppm.provider.SettingsProvider;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    private final UserService userService;
    private final CryptoProvider cryptoProvider;
    private final SettingsProvider settingsProvider;

    public DatabaseServiceImpl(UserService userService, CryptoProvider cryptoProvider, SettingsProvider settingsProvider) {
        this.userService = userService;
        this.cryptoProvider = cryptoProvider;
        this.settingsProvider = settingsProvider;
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
            DbKey key = cryptoProvider.generateDbKey();
            settingsProvider.setDBEncryptionKeyId(key.getId());
            cryptoProvider.installDbKey(key.getKey());
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
            if (settingsProvider.getDBEncryptionKeyId() != id) {
                return MessageDto.builder().message("db10").build();
            }
            byte[] dbKey = Base64.getDecoder().decode(json.get("key").asString());
            cryptoProvider.installDbKey(dbKey);
        }
        return MessageDto.builder().build();
    }

    private DbStatus getDBEncryptionStatus() {
        if (!cryptoProvider.isSystemClosed()) {
            return DbStatus.OK;
        }
        if (settingsProvider.getDBEncryptionKeyId() == null) {
            return DbStatus.NEW_DB;
        }
        return DbStatus.NEED_KEY;
    }
}

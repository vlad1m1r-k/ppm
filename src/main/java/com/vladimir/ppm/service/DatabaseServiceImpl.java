package com.vladimir.ppm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.DbStatus;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.stream.StreamSupport;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    private final UserService userService;
    private final CryptoProvider cryptoProvider;
    private final SettingsService settingsService;

    public DatabaseServiceImpl(UserService userService, CryptoProvider cryptoProvider, SettingsService settingsService) {
        this.userService = userService;
        this.cryptoProvider = cryptoProvider;
        this.settingsService = settingsService;
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
            settingsService.setDBEncryptionKeyId(key.getId());
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
            if (settingsService.getDBEncryptionKeyId() != id) {
                return MessageDto.builder().message("db10").build();
            }
            Byte[] dbKey = StreamSupport.stream(json.get("key").spliterator(), false).map(o -> Byte.valueOf(String.valueOf(o))).toArray(Byte[]::new);
            cryptoProvider.installDbKey(ArrayUtils.toPrimitive(dbKey));
        }
        return MessageDto.builder().build();
    }

    private DbStatus getDBEncryptionStatus() {
        if (!cryptoProvider.isSystemClosed()) {
            return DbStatus.OK;
        }
        if (settingsService.getDBEncryptionKeyId() == null) {
            return DbStatus.NEW_DB;
        }
        return DbStatus.NEED_KEY;
    }
}

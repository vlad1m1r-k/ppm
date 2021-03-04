package com.vladimir.ppm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.DbStatus;
import com.vladimir.ppm.domain.Settings;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.repository.SettingsRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.stream.StreamSupport;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final UserService userService;
    private final CryptoProvider cryptoProvider;
    private final SettingsRepository settingsRepository;

    public SettingsServiceImpl(UserService userService, CryptoProvider cryptoProvider, SettingsRepository settingsRepository) {
        this.userService = userService;
        this.cryptoProvider = cryptoProvider;
        this.settingsRepository = settingsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto getDbStatus(Token token) {
        if (userService.isAdmin(token)) {
            return MessageDto.builder()
                    .message(getDbStatus().name())
                    .build();
        }
        return null;
    }

    @Override
    @Transactional
    public MessageDto generateDbKey(Token token) {
        if (userService.isAdmin(token) && getDbStatus() == DbStatus.NEW_DB) {
            DbKey key = cryptoProvider.generateDbKey();
            Settings settings = settingsRepository.getOne(1L);
            settings.setEncryptionKeyId(key.getId());
            cryptoProvider.installDbKey(key.getKey());
            return MessageDto.builder()
                    .message(key.toString())
                    .build();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto installDbKey(Token token, String key) throws IOException {
        if (userService.isAdmin(token) && getDbStatus() == DbStatus.NEED_KEY) {
            JsonNode json = new ObjectMapper().readTree(new String(Base64.getDecoder()
                    .decode(key.replaceAll("\n", "").replaceAll("\r", ""))));
            long id = json.get("id").longValue();
            Byte[] dbKey = StreamSupport.stream(json.get("key").spliterator(), false).map(o -> Byte.valueOf(String.valueOf(o))).toArray(Byte[]::new);
            Settings settings = settingsRepository.getOne(1L);
            if (settings.getEncryptionKeyId() != id) {
                return MessageDto.builder().message("db10").build();
            }
            cryptoProvider.installDbKey(ArrayUtils.toPrimitive(dbKey));
        }
        return MessageDto.builder().build();
    }

    private DbStatus getDbStatus() {
        if (!cryptoProvider.isSystemClosed()) {
            return DbStatus.OK;
        }
        Settings settings = settingsRepository.getOne(1L);
        if (settings.getEncryptionKeyId() == null) {
            return DbStatus.NEW_DB;
        }
        return DbStatus.NEED_KEY;
    }
}

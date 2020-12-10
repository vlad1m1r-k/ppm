package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.DbKey;
import com.vladimir.ppm.domain.DbStatus;
import com.vladimir.ppm.domain.Settings;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final UserService userService;
    private final CryptoProviderService cryptoProviderService;
    private final SettingsRepository settingsRepository;

    public SettingsServiceImpl(UserService userService, CryptoProviderService cryptoProviderService, SettingsRepository settingsRepository) {
        this.userService = userService;
        this.cryptoProviderService = cryptoProviderService;
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
            DbKey key = cryptoProviderService.generateDbKey();
            Settings settings = settingsRepository.getOne(1L);
            settings.setEncryptionKeyId(key.getId());
            cryptoProviderService.installDbKey(key.getKey(), key.getIv());
            return MessageDto.builder()
                    .message(key.toString())
                    .build();
        }
        return null;
    }

    private DbStatus getDbStatus() {
        if (!cryptoProviderService.isSystemClosed()) {
            return DbStatus.OK;
        }
        Settings settings = settingsRepository.getOne(1L);
        if (settings.getEncryptionKeyId() == null) {
            return DbStatus.NEW_DB;
        }
        return DbStatus.NEED_KEY;
    }
}

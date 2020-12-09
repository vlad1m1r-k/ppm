package com.vladimir.ppm.service;

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
    @Transactional
    public MessageDto getDbStatus(Token token) {
        if (userService.isAdmin(token)) {
            if (!cryptoProviderService.isSystemClosed()) {
                return MessageDto.builder()
                        .message(DbStatus.OK.name())
                        .build();
            }
            Settings settings = settingsRepository.getOne(1L);
            if (settings.getEncryptionKeyId() == null) {
                return MessageDto.builder()
                        .message(DbStatus.NEW_DB.name())
                        .build();
            }
            return MessageDto.builder()
                    .message(DbStatus.NEED_KEY.name())
                    .build();
        }
        return null;
    }
}

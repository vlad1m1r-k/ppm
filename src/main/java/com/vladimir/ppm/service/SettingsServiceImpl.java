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
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Base64;
import java.util.stream.StreamSupport;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final UserService userService;
    private final CryptoProvider cryptoProvider;
    private final SettingsRepository settingsRepository;
    private final EntityManagerFactory managerFactory;
    private Settings settings;

    public SettingsServiceImpl(@Lazy UserService userService, @Lazy CryptoProvider cryptoProvider, SettingsRepository settingsRepository, EntityManagerFactory managerFactory) {
        this.userService = userService;
        this.cryptoProvider = cryptoProvider;
        this.settingsRepository = settingsRepository;
        this.managerFactory = managerFactory;
    }

    @PostConstruct
    private void init() {
        SessionFactory sessionFactory = managerFactory.unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        this.settings = Hibernate.unproxy(session.get(Settings.class, 1L), Settings.class);
        session.close();
    }

    @Override
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
            this.settings.setEncryptionKeyId(key.getId());
            settings.setEncryptionKeyId(key.getId());
            cryptoProvider.installDbKey(key.getKey());
            return MessageDto.builder()
                    .message(key.toString())
                    .build();
        }
        return null;
    }

    @Override
    public MessageDto installDbKey(Token token, String key) throws IOException {
        if (userService.isAdmin(token) && getDbStatus() == DbStatus.NEED_KEY) {
            JsonNode json = new ObjectMapper().readTree(new String(Base64.getDecoder()
                    .decode(key.replaceAll("\n", "").replaceAll("\r", ""))));
            long id = json.get("id").longValue();
            Byte[] dbKey = StreamSupport.stream(json.get("key").spliterator(), false).map(o -> Byte.valueOf(String.valueOf(o))).toArray(Byte[]::new);
            if (settings.getEncryptionKeyId() != id) {
                return MessageDto.builder().message("db10").build();
            }
            cryptoProvider.installDbKey(ArrayUtils.toPrimitive(dbKey));
        }
        return MessageDto.builder().build();
    }

    @Override
    public int getServerKeyLifeTimeDays() {
        return settings.getServerKeyLifeTimeDays();
    }

    @Override
    @Transactional
    public boolean setServerKeyLifeTimeDays(int lifeTime) {
        //TODO
        //TODO validate
        //TODO Change settings event
        return false;
    }

    @Override
    public int getTokenLifeTimeMinutes() {
        return settings.getTokenLifeTimeMinutes();
    }

    @Override
    @Transactional
    public boolean setTokenLifeTimeMinutes(int lifeTime) {
        //TODO
        return false;
    }

    @Override
    public long getDBEncryptionKeyId() {
        //TODO
        return 0;
    }

    @Override
    public boolean setDBEncryptionKeyId(long id) {
        //TODO
        return false;
    }

    private DbStatus getDbStatus() {
        if (!cryptoProvider.isSystemClosed()) {
            return DbStatus.OK;
        }
        if (settings.getEncryptionKeyId() == null) {
            return DbStatus.NEW_DB;
        }
        return DbStatus.NEED_KEY;
    }
}

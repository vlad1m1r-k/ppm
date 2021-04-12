package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Settings;
import com.vladimir.ppm.repository.SettingsRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@Service
public class SettingsProviderImpl implements SettingsProvider {
    private final SettingsRepository settingsRepository;
    private final EntityManagerFactory managerFactory;
    private Settings settings;

    public SettingsProviderImpl(SettingsRepository settingsRepository, EntityManagerFactory managerFactory) {
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
    public int getServerKeyLifeTimeDays() {
        return settings.getServerKeyLifeTimeDays();
    }

    @Override
    @Transactional
    public void setServerKeyLifeTimeDays(int lifeTime) {
        this.settings.setServerKeyLifeTimeDays(lifeTime);
        Settings settings = settingsRepository.getOne(1L);
        settings.setServerKeyLifeTimeDays(lifeTime);
    }

    @Override
    public int getTokenLifeTimeMinutes() {
        return settings.getTokenLifeTimeMinutes();
    }

    @Override
    @Transactional
    public void setTokenLifeTimeMinutes(int lifeTime) {
        this.settings.setTokenLifeTimeMinutes(lifeTime);
        Settings settings = settingsRepository.getOne(1L);
        settings.setTokenLifeTimeMinutes(lifeTime);
    }

    @Override
    public Long getDBEncryptionKeyId() {
        return settings.getEncryptionKeyId();
    }

    @Override
    @Transactional
    public void setDBEncryptionKeyId(long id) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setEncryptionKeyId(id);
        settings.setEncryptionKeyId(id);
    }
}

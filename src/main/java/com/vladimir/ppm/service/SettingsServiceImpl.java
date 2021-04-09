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
public class SettingsServiceImpl implements SettingsService {
    private final SettingsRepository settingsRepository;
    private final EntityManagerFactory managerFactory;
    private Settings settings;

    public SettingsServiceImpl(SettingsRepository settingsRepository, EntityManagerFactory managerFactory) {
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

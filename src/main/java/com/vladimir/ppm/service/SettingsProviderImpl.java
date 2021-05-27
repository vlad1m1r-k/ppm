package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Settings;
import com.vladimir.ppm.repository.SettingsRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.util.Set;

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
    public int getTokenLifeTimeMinutes() {
        return settings.getTokenLifeTimeMinutes();
    }

    @Override
    public Long getDBEncryptionKeyId() {
        return settings.getEncryptionKeyId();
    }

    @Override
    public int getPwdMinLength() {
        return settings.getPwdMinLength();
    }

    @Override
    public boolean getPwdComplexity() {
        return settings.getPwdComplexity();
    }

    @Override
    public boolean getPwdSpecialChar() {
        return settings.getPwdSpecialChar();
    }

    @Override
    public int getIncorrectLoginAttempts() {
        return settings.getIncorrectLoginAttempts();
    }

    @Override
    public int getIpBanTimeDays() {
        return settings.getIpBanTimeDays();
    }

    @Override
    public int getIncorrectPasswdAttempts() {
        return settings.getIncorrectPasswdAttempts();
    }

    @Override
    public Set<String> getIpBlacklist() {
        return settings.getIpBlackList();
    }

    @Override
    public Set<String> getIpWhiteList() {
        return settings.getIpWhiteList();
    }

    @Override
    @Transactional
    public void setServerKeyLifeTimeDays(int lifeTime) {
        this.settings.setServerKeyLifeTimeDays(lifeTime);
        Settings settings = settingsRepository.getOne(1L);
        settings.setServerKeyLifeTimeDays(lifeTime);
    }

    @Override
    @Transactional
    public void setTokenLifeTimeMinutes(int lifeTime) {
        this.settings.setTokenLifeTimeMinutes(lifeTime);
        Settings settings = settingsRepository.getOne(1L);
        settings.setTokenLifeTimeMinutes(lifeTime);
    }

    @Override
    @Transactional
    public void setDBEncryptionKeyId(long id) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setEncryptionKeyId(id);
        settings.setEncryptionKeyId(id);
    }

    @Override
    @Transactional
    public void setPwdMinLength(int minLength) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setPwdMinLength(minLength);
        settings.setPwdMinLength(minLength);
    }

    @Override
    @Transactional
    public void setPwdComplexity(boolean complexity) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setPwdComplexity(complexity);
        settings.setPwdComplexity(complexity);
    }

    @Override
    @Transactional
    public void setPwdSpecialChar(boolean specialChar) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setPwdSpecialChar(specialChar);
        settings.setPwdSpecialChar(specialChar);
    }

    @Override
    @Transactional
    public void setIncorrectLoginAttempts(int attempts) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setIncorrectLoginAttempts(attempts);
        settings.setIncorrectLoginAttempts(attempts);
    }

    @Override
    @Transactional
    public void setIpBanTimeDays(int days) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setIpBanTimeDays(days);
        settings.setIpBanTimeDays(days);
    }

    @Override
    @Transactional
    public void setIncorrectPasswdAttempts(int attempts) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.setIncorrectPasswdAttempts(attempts);
        settings.setIncorrectPasswdAttempts(attempts);
    }

    @Override
    @Transactional
    public void addIpToBlackList(String ip) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.getIpBlackList().add(ip);
        settings.getIpBlackList().add(ip);
    }

    @Override
    @Transactional
    public void addIpToWhiteList(String ip) {
        Settings settings = settingsRepository.getOne(1L);
        this.settings.getIpWhiteList().add(ip);
        settings.getIpWhiteList().add(ip);
    }

    @Override
    public boolean isIpWhitelisted(String ip) {
        for (String address : getIpWhiteList()) {
            IpAddressMatcher matcher = new IpAddressMatcher(address);
            if (matcher.matches(ip)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isIpBlackListed(String ip) {
        for (String address : getIpBlacklist()) {
            IpAddressMatcher matcher = new IpAddressMatcher(address);
            if (matcher.matches(ip)) {
                return true;
            }
        }
        return false;
    }
}

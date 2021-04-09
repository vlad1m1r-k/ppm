package com.vladimir.ppm.domain;

import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long encryptionKeyId;
    private volatile Integer serverKeyLifeTimeDays;
    private volatile Integer tokenLifeTimeMinutes;

    public Long getId() {
        return id;
    }

    public Long getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public Integer getServerKeyLifeTimeDays() {
        return serverKeyLifeTimeDays;
    }

    public Integer getTokenLifeTimeMinutes() {
        return tokenLifeTimeMinutes;
    }

    public void setEncryptionKeyId(Long encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }

    public void setServerKeyLifeTimeDays(Integer serverKeyLifeTimeDays) {
        this.serverKeyLifeTimeDays = serverKeyLifeTimeDays;
    }

    public void setTokenLifeTimeMinutes(Integer tokenLifeTimeMinutes) {
        this.tokenLifeTimeMinutes = tokenLifeTimeMinutes;
    }
}

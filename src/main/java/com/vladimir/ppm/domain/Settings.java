package com.vladimir.ppm.domain;

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
    private volatile Integer pwdMinLength;
    private volatile Boolean pwdComplexity;
    private volatile Boolean pwdSpecialChar;

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

    public Integer getPwdMinLength() {
        return pwdMinLength;
    }

    public Boolean getPwdComplexity() {
        return pwdComplexity;
    }

    public Boolean getPwdSpecialChar() {
        return pwdSpecialChar;
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

    public void setPwdMinLength(Integer pwdMinLength) {
        this.pwdMinLength = pwdMinLength;
    }

    public void setPwdComplexity(Boolean pwdComplexity) {
        this.pwdComplexity = pwdComplexity;
    }

    public void setPwdSpecialChar(Boolean pwdSpecialChar) {
        this.pwdSpecialChar = pwdSpecialChar;
    }
}

package com.vladimir.ppm.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

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
    private volatile Integer incorrectLoginAttempts;
    private volatile Integer ipBanTimeDays;

    @ElementCollection
    private final Set<String> ipBlackList = new HashSet<>();

    @ElementCollection
    private final Set<String> ipWhiteList = new HashSet<>();

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

    public Integer getIncorrectLoginAttempts() {
        return incorrectLoginAttempts;
    }

    public Integer getIpBanTimeDays() {
        return ipBanTimeDays;
    }

    public Set<String> getIpBlackList() {
        return ipBlackList;
    }

    public Set<String> getIpWhiteList() {
        return ipWhiteList;
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

    public void setIncorrectLoginAttempts(Integer incorrectLoginAttempt) {
        this.incorrectLoginAttempts = incorrectLoginAttempt;
    }

    public void setIpBanTimeDays(Integer ipBanTimeDays) {
        this.ipBanTimeDays = ipBanTimeDays;
    }
}

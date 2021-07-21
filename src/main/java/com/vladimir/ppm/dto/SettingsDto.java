package com.vladimir.ppm.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsDto {
    private final Integer serverKeyLifeTimeDays;
    private final Integer tokenLifeTimeMinutes;
    private final Integer pwdMinLength;
    private final Boolean pwdComplexity;
    private final Boolean pwdSpecialChar;
    private final Integer incorrectLoginAttempts;
    private final Integer ipBanTimeDays;
    private final Integer incorrectPasswdAttempts;
    private final Integer logLifeTime;

    private SettingsDto(Builder builder) {
        this.serverKeyLifeTimeDays = builder.serverKeyLifeTimeDays;
        this.tokenLifeTimeMinutes = builder.tokenLifeTimeMinutes;
        this.pwdMinLength = builder.pwdMinLength;
        this.pwdComplexity = builder.pwdComplexity;
        this.pwdSpecialChar = builder.pwdSpecialChar;
        this.incorrectLoginAttempts = builder.incorrectLoginAttempts;
        this.ipBanTimeDays = builder.ipBanTimeDays;
        this.incorrectPasswdAttempts = builder.incorrectPasswdAttempts;
        this.logLifeTime = builder.logLifeTime;
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

    public Integer getIncorrectPasswdAttempts() {
        return incorrectPasswdAttempts;
    }

    public Integer getLogLifeTime() {
        return logLifeTime;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer serverKeyLifeTimeDays;
        private Integer tokenLifeTimeMinutes;
        private Integer pwdMinLength;
        private Boolean pwdComplexity;
        private Boolean pwdSpecialChar;
        private Integer incorrectLoginAttempts;
        private Integer ipBanTimeDays;
        private Integer incorrectPasswdAttempts;
        private Integer logLifeTime;

        public Builder serverKeyLifeTimeDays(Integer serverKeyLifeTimeDays) {
            this.serverKeyLifeTimeDays = serverKeyLifeTimeDays;
            return this;
        }

        public Builder tokenLifeTimeMinutes(Integer tokenLifeTimeMinutes) {
            this.tokenLifeTimeMinutes = tokenLifeTimeMinutes;
            return this;
        }

        public Builder pwdMinLength(Integer pwdMinLength) {
            this.pwdMinLength = pwdMinLength;
            return this;
        }

        public Builder pwdComplexity(Boolean pwdComplexity) {
            this.pwdComplexity = pwdComplexity;
            return this;
        }

        public Builder pwdSpecialChar(Boolean pwdSpecialChar) {
            this.pwdSpecialChar = pwdSpecialChar;
            return this;
        }

        public Builder incorrectLoginAttempts(Integer incorrectLoginAttempts) {
            this.incorrectLoginAttempts = incorrectLoginAttempts;
            return this;
        }

        public Builder ipBanTimeDays(Integer ipBanTimeDays) {
            this.ipBanTimeDays = ipBanTimeDays;
            return this;
        }

        public Builder incorrectPasswdAttempts(Integer incorrectPasswdAttempts) {
            this.incorrectPasswdAttempts = incorrectPasswdAttempts;
            return this;
        }

        public Builder logLifeTime(Integer logLifeTime) {
            this.logLifeTime = logLifeTime;
            return this;
        }

        public SettingsDto build() {
            return new SettingsDto(this);
        }
    }
}

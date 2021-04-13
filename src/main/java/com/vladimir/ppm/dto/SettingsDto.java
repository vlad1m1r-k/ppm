package com.vladimir.ppm.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsDto {
    private final Integer serverKeyLifeTimeDays;
    private final Integer tokenLifeTimeMinutes;
    private final Integer pwdMinLength;
    private final Boolean pwdComplexity;
    private final Boolean pwdSpecialChar;

    private SettingsDto(Builder builder) {
        this.serverKeyLifeTimeDays = builder.serverKeyLifeTimeDays;
        this.tokenLifeTimeMinutes = builder.tokenLifeTimeMinutes;
        this.pwdMinLength = builder.pwdMinLength;
        this.pwdComplexity = builder.pwdComplexity;
        this.pwdSpecialChar = builder.pwdSpecialChar;
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

        public SettingsDto build() {
            return new SettingsDto(this);
        }
    }
}

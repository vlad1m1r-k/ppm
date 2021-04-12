package com.vladimir.ppm.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsDto {
    private final Integer serverKeyLifeTimeDays;
    private final Integer tokenLifeTimeMinutes;

    private SettingsDto(Builder builder) {
        this.serverKeyLifeTimeDays = builder.serverKeyLifeTimeDays;
        this.tokenLifeTimeMinutes = builder.tokenLifeTimeMinutes;
    }

    public Integer getServerKeyLifeTimeDays() {
        return serverKeyLifeTimeDays;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getTokenLifeTimeMinutes() {
        return tokenLifeTimeMinutes;
    }

    public static class Builder {
        private Integer serverKeyLifeTimeDays;
        private Integer tokenLifeTimeMinutes;

        public Builder serverKeyLifeTimeDays(Integer serverKeyLifeTimeDays) {
            this.serverKeyLifeTimeDays = serverKeyLifeTimeDays;
            return this;
        }

        public Builder tokenLifeTimeMinutes(Integer tokenLifeTimeMinutes) {
            this.tokenLifeTimeMinutes = tokenLifeTimeMinutes;
            return this;
        }

        public SettingsDto build() {
            return new SettingsDto(this);
        }
    }
}

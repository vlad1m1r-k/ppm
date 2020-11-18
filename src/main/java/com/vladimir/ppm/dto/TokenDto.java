package com.vladimir.ppm.dto;

import org.json.JSONObject;

public class TokenDto {
    private final Long lifeTime;
    private final String message;
    private final String token;
    private final Boolean adminSettings;

    private TokenDto(Builder builder) {
        this.lifeTime = builder.lifeTime;
        this.message = builder.message;
        this.token = builder.token;
        this.adminSettings = builder.adminSettings;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public Boolean getAdminSettings() {
        return adminSettings;
    }

    public String toJson() {
        return new JSONObject(this).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long lifeTime;
        private String message;
        private String token;
        private Boolean adminSettings;

        public Builder lifeTime(Long lifeTime) {
            this.lifeTime = lifeTime;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder adminSettings(boolean adminSettings) {
            this.adminSettings = adminSettings;
            return this;
        }

        public TokenDto build() {
            return new TokenDto(this);
        }
    }
}

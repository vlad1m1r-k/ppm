package com.vladimir.ppm.dto;

import org.json.JSONObject;

public class TokenDto {
    private final Long lifeTime;
    private final String message;
    private final String token;
    private final Boolean adminSettings;
    private final Boolean systemClosed;

    private TokenDto(Builder builder) {
        this.lifeTime = builder.lifeTime;
        this.message = builder.message;
        this.token = builder.token;
        this.adminSettings = builder.adminSettings;
        this.systemClosed = builder.systemClosed;
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

    public Boolean getSystemClosed() {
        return systemClosed;
    }

    public static class Builder {
        private Long lifeTime;
        private String message;
        private String token;
        private Boolean adminSettings;
        private Boolean systemClosed;

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

        public Builder systemClosed(boolean systemClosed) {
            this.systemClosed = systemClosed;
            return this;
        }

        public TokenDto build() {
            return new TokenDto(this);
        }
    }
}

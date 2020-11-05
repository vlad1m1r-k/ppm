package com.vladimir.ppm.dto;

public class TokenDto {
    private final String lifeTime;
    private final String message;
    private final String token;

    private TokenDto(Builder builder) {
        this.lifeTime = builder.lifeTime;
        this.message = builder.message;
        this.token = builder.token;
    }

    public String getLifeTime() {
        return lifeTime;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String lifeTime;
        private String message;
        private String token;

        public Builder lifeTime(String lifeTime) {
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

        public TokenDto build() {
            return new TokenDto(this);
        }
    }
}

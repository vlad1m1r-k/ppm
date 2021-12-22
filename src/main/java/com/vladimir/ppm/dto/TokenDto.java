package com.vladimir.ppm.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenDto {
    private final Long lifeTime;
    private final String message;
    private final String token;
    private final Boolean adminSettings;
    private final Boolean systemClosed;
    private final Boolean changePwd;

    private TokenDto(Builder builder) {
        this.lifeTime = builder.lifeTime;
        this.message = builder.message;
        this.token = builder.token;
        this.adminSettings = builder.adminSettings;
        this.systemClosed = builder.systemClosed;
        this.changePwd = builder.changePwd;
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

    public static Builder builder() {
        return new Builder();
    }

    public Boolean getSystemClosed() {
        return systemClosed;
    }
    
    public Boolean getChangePwd() {
		return changePwd;
	}

	public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static class Builder {
        private Long lifeTime;
        private String message;
        private String token;
        private Boolean adminSettings;
        private Boolean systemClosed;
        private Boolean changePwd;

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
        
        public Builder changePwd(boolean changePwd) {
        	this.changePwd = changePwd;
        	return this;
        }

        public TokenDto build() {
            return new TokenDto(this);
        }
    }
}

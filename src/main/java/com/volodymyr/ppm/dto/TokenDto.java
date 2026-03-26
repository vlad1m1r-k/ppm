package com.volodymyr.ppm.dto;

import tools.jackson.databind.ObjectMapper;

public class TokenDto {
    private final Long lifeTime;
    private final String message;
    private final String token;
    private final Boolean adminSettings;
    private final Boolean systemClosed;
    private final Boolean changePwd;
    private final Boolean tfaRequired;
    
    private TokenDto(Builder builder) {
        this.lifeTime = builder.lifeTime;
        this.message = builder.message;
        this.token = builder.token;
        this.adminSettings = builder.adminSettings;
        this.systemClosed = builder.systemClosed;
        this.changePwd = builder.changePwd;
        this.tfaRequired = builder.tfaRequired;
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
    
    public Boolean gettfaRequired() {
		return tfaRequired;
	}

	public String toJson() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static class Builder {
        private Long lifeTime;
        private String message;
        private String token;
        private Boolean adminSettings;
        private Boolean systemClosed;
        private Boolean changePwd;
        private Boolean tfaRequired;

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
        
        public Builder tfaRequired(boolean tfaRequired) {
        	this.tfaRequired = tfaRequired;
        	return this;
        }

        public TokenDto build() {
            return new TokenDto(this);
        }
    }
}

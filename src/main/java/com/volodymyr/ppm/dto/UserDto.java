package com.volodymyr.ppm.dto;

import java.util.List;

import com.volodymyr.ppm.domain.UserStatus;
import com.volodymyr.ppm.domain.UserTfaStatus;

public class UserDto {
    private final Long id;
    private final String login;
    private final List<GroupDto> groups;
    private final UserStatus status;
    private final Boolean changePwd;
    private final UserTfaStatus tfaStatus;

    private UserDto(Builder builder) {
        this.id = builder.id;
        this.login = builder.login;
        this.groups = builder.groups;
        this.status = builder.status;
        this.changePwd = builder.changePwd;
        this.tfaStatus = builder.tfaStatus;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<GroupDto> getGroups() {
        return groups;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Boolean getChangePwd() {
		return changePwd;
	}
    
    public UserTfaStatus getTfaStatus() {
    	return tfaStatus;
    }

	public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String login;
        private List<GroupDto> groups;
        private UserStatus status;
        private Boolean changePwd;
        private UserTfaStatus tfaStatus;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder groups(List<GroupDto> groups) {
            this.groups = groups;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder changePwd(Boolean changePwd) {
        	this.changePwd = changePwd;
        	return this;
        }
        
        public Builder tfaStatus(UserTfaStatus tfaStatus) {
        	this.tfaStatus = tfaStatus;
        	return this;
        }

        public UserDto build() {
            return new UserDto(this);
        }
    }
}

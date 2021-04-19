package com.vladimir.ppm.dto;

import java.util.List;

public class GroupDto {
    private final Long id;
    private final String name;
    private final boolean adminSettings;
    private final List<UserDto> users;

    private GroupDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.adminSettings = builder.adminSettings;
        this.users = builder.users;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAdminSettings() {
        return adminSettings;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private boolean adminSettings = false;
        private List<UserDto> users;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder adminSettings(boolean adminSettings) {
            this.adminSettings = adminSettings;
            return this;
        }

        public Builder users(List<UserDto> users) {
            this.users = users;
            return this;
        }

        public GroupDto build() {
            return new GroupDto(this);
        }
    }
}

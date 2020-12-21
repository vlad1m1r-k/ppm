package com.vladimir.ppm.dto;

import org.json.JSONObject;

public class PasswordDto implements Comparable<PasswordDto> {
    private final Long id;
    private final String name;
    private final String login;
    private final String password;
    private final String note;

    private PasswordDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.login = builder.login;
        this.password = builder.password;
        this.note = builder.note;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNote() {
        return note;
    }

    public String toJson() {
        return new JSONObject(this).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int compareTo(PasswordDto passwordDto) {
        return this.name.compareTo(passwordDto.getName());
    }

    public static class Builder {
        private Long id;
        private String name;
        private String login;
        private String password;
        private String note;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public PasswordDto build() {
            return new PasswordDto(this);
        }
    }
}

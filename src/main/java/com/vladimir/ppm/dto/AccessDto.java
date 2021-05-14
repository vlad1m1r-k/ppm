package com.vladimir.ppm.dto;

import com.vladimir.ppm.domain.Access;

public class AccessDto {
    private final Long id;
    private final String name;
    private final Access access;

    private AccessDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.access = builder.access;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Access getAccess() {
        return access;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Access access;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder access(Access access) {
            this.access = access;
            return this;
        }

        public AccessDto build() {
            return new AccessDto(this);
        }
    }
}

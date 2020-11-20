package com.vladimir.ppm.dto;

import com.vladimir.ppm.domain.Access;
import org.json.JSONObject;

import java.util.Set;

public class ContainerDto {
    private Long id;
    private String name;
    private Set<ContainerDto> children;
    private Access access;

    private ContainerDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.children = builder.children;
        this.access = builder.access;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<ContainerDto> getChildren() {
        return children;
    }

    public Access getAccess() {
        return access;
    }

    public String toJson() {
        return new JSONObject(this).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Set<ContainerDto> children;
        private Access access;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder children(Set<ContainerDto> children) {
            this.children = children;
            return this;
        }

        public Builder access(Access access) {
            this.access = access;
            return this;
        }

        public ContainerDto build() {
            return new ContainerDto(this);
        }
    }
}

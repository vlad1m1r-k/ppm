package com.vladimir.ppm.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class AccessTreeDto {
    private final Long id;
    private final String name;
    private final List<AccessTreeDto> children;
    private final Boolean accessNA;
    private final Boolean accessPT;
    private final Boolean accessRO;
    private final Boolean accessRW;

    private AccessTreeDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.children = builder.children;
        this.accessNA = builder.accessNA;
        this.accessPT = builder.accessPT;
        this.accessRO = builder.accessRO;
        this.accessRW = builder.accessRW;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<AccessTreeDto> getChildren() {
        return children;
    }

    public Boolean getAccessNA() {
        return accessNA;
    }

    public Boolean getAccessPT() {
        return accessPT;
    }

    public Boolean getAccessRO() {
        return accessRO;
    }

    public Boolean getAccessRW() {
        return accessRW;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static class Builder {
        private Long id;
        private String name;
        private List<AccessTreeDto> children;
        private Boolean accessNA;
        private Boolean accessPT;
        private Boolean accessRO;
        private Boolean accessRW;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder children(List<AccessTreeDto> children) {
            this.children = children;
            return this;
        }

        public Builder accessNA(Boolean accessNA) {
            this.accessNA = accessNA;
            return this;
        }

        public Builder accessPT(Boolean accessPT) {
            this.accessPT = accessPT;
            return this;
        }

        public Builder accessRO(Boolean accessRO) {
            this.accessRO = accessRO;
            return this;
        }

        public Builder accessRW(Boolean accessRW) {
            this.accessRW = accessRW;
            return this;
        }

        public AccessTreeDto build() {
            return new AccessTreeDto(this);
        }
    }
}

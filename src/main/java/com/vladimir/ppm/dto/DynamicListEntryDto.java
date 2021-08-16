package com.vladimir.ppm.dto;

import java.util.Date;

public class DynamicListEntryDto {
    private final String ip;
    private final String expire;

    private DynamicListEntryDto(Builder builder) {
        this.ip = builder.ip;
        this.expire = builder.expire;
    }

    public String getIp() {
        return ip;
    }

    public String getExpire() {
        return expire;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String ip;
        private String expire;

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder expire(long unbanTime) {
            this.expire = (new Date(unbanTime)).toString();
            return this;
        }

        public DynamicListEntryDto build() {
            return new DynamicListEntryDto(this);
        }
    }
}

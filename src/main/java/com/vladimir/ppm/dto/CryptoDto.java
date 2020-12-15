package com.vladimir.ppm.dto;

public class CryptoDto {
    private final String key;
    private final String data;

    private CryptoDto(Builder builder) {
        this.key = builder.key;
        this.data = builder.data;
    }

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String key;
        private String data;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public CryptoDto build() {
            return new CryptoDto(this);
        }
    }
}

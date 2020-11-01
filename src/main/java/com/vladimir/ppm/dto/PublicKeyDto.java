package com.vladimir.ppm.dto;

public class PublicKeyDto {
    private Long keyPairExpireDate;
    private String keyPEM;

    private PublicKeyDto(Builder builder) {
        this.keyPairExpireDate = builder.keyPairExpireDate;
        this.keyPEM = builder.keyPEM;
    }

    public Long getKeyPairExpireDate() {
        return keyPairExpireDate;
    }

    public String getKeyPEM() {
        return keyPEM;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long keyPairExpireDate;
        private String keyPEM;

        public Builder keyPairExpireDate(Long keyPairExpireDate) {
            this.keyPairExpireDate = keyPairExpireDate;
            return this;
        }

        public Builder keyPEM(String keyPEM) {
            this.keyPEM = keyPEM;
            return this;
        }

        public PublicKeyDto build() {
            return new PublicKeyDto(this);
        }
    }
}

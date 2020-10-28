package com.vladimir.ppm.dto;

public class PublicKeyDto {
    private Long keyPairExpireDate;
    private String publicKey;

    private PublicKeyDto(Builder builder) {
        this.keyPairExpireDate = builder.keyPairExpireDate;
        this.publicKey = builder.publicKey;
    }

    public Long getKeyPairExpireDate() {
        return keyPairExpireDate;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long keyPairExpireDate;
        private String publicKey;

        public Builder keyPairExpireDate(Long keyPairExpireDate) {
            this.keyPairExpireDate = keyPairExpireDate;
            return this;
        }

        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public PublicKeyDto build() {
            return new PublicKeyDto(this);
        }
    }
}

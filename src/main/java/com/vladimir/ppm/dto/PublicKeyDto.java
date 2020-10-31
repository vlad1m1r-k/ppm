package com.vladimir.ppm.dto;

public class PublicKeyDto {
    private Long keyPairExpireDate;
    private String modulus;
    private String exponent;

    private PublicKeyDto(Builder builder) {
        this.keyPairExpireDate = builder.keyPairExpireDate;
        this.modulus = builder.modulus;
        this.exponent = builder.exponent;
    }

    public Long getKeyPairExpireDate() {
        return keyPairExpireDate;
    }

    public String getModulus() {
        return modulus;
    }

    public String getExponent() {
        return exponent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long keyPairExpireDate;
        private String modulus;
        private String exponent;

        public Builder keyPairExpireDate(Long keyPairExpireDate) {
            this.keyPairExpireDate = keyPairExpireDate;
            return this;
        }

        public Builder modulus(String modulus) {
            this.modulus = modulus;
            return this;
        }

        public Builder exponent(String exponent) {
            this.exponent = exponent;
            return this;
        }

        public PublicKeyDto build() {
            return new PublicKeyDto(this);
        }
    }
}

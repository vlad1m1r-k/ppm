package com.vladimir.ppm.dto;

public class PasswordDto implements Comparable<PasswordDto> {
    private final Long id;
    private final String name;

    private PasswordDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public PasswordDto build() {
            return new PasswordDto(this);
        }
    }
}

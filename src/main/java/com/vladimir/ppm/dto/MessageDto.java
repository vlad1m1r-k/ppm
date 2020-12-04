package com.vladimir.ppm.dto;

import org.json.JSONObject;

public class MessageDto {
    private final String message;

    private MessageDto(Builder builder) {
        this.message = builder.message;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() {
        return new JSONObject(this).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public MessageDto build() {
            return new MessageDto(this);
        }
    }
}

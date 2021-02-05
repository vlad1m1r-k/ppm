package com.vladimir.ppm.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageDto {
    private final String message;

    private MessageDto(Builder builder) {
        this.message = builder.message;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
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

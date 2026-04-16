package com.volodymyr.ppm.dto;

import tools.jackson.databind.ObjectMapper;

public class MessageDto {
    private final String message;
    private final String data;

    private MessageDto(Builder builder) {
        this.message = builder.message;
        this.data = builder.data;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
		return data;
	}

	public String toJson() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;
        private String data;

        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder data(String data) {
        	this.data = data;
        	return this;
        }

        public MessageDto build() {
            return new MessageDto(this);
        }
    }
}

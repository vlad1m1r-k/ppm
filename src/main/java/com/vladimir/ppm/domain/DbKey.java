package com.vladimir.ppm.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class DbKey {
    private final long id;
    private final byte[] key;

    public DbKey(long id, byte[] key) {
        this.id = id;
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public byte[] getKey() {
        return key;
    }

    @Override
    public String toString() {
        try {
            return Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsString(this).getBytes()).replaceAll("(.{64})", "$1\n");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

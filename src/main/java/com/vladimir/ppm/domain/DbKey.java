package com.vladimir.ppm.domain;

import org.json.JSONObject;

import java.util.Base64;

public class DbKey {
    private long id;
    private byte[] key;

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
        return Base64.getEncoder().encodeToString(new JSONObject(this).toString().getBytes()).replaceAll("(.{64})", "$1\n");
    }
}

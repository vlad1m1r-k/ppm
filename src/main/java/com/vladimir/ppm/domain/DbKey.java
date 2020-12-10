package com.vladimir.ppm.domain;

import org.json.JSONObject;

import java.util.Base64;

public class DbKey {
    private long id;
    private byte[] key;
    private byte[] iv;

    public DbKey(long id, byte[] key, byte[] iv) {
        this.id = id;
        this.key = key;
        this.iv = iv;
    }

    public long getId() {
        return id;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }

    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(new JSONObject(this).toString().getBytes()).replaceAll("(.{64})", "$1\n");
    }
}

package com.vladimir.ppm.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long encryptionKeyId;

    public Long getId() {
        return id;
    }

    public Long getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(Long encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }
}

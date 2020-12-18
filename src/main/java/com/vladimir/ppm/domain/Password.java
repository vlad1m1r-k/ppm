package com.vladimir.ppm.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "passwords")
public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean deleted = false;

    @Lob
    private byte[] encryptedLogin;

    @Lob
    private byte[] encryptedPass;

    @Lob
    private byte[] encryptedNote;

    @ManyToOne
    private Container parent;

    public Password() {}

    public Password(String name, Container parent) {
        this.name = name;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public byte[] getEncryptedLogin() {
        return encryptedLogin;
    }

    public byte[] getEncryptedPass() {
        return encryptedPass;
    }

    public byte[] getEncryptedNote() {
        return encryptedNote;
    }

    public Container getParent() {
        return parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setEncryptedLogin(byte[] encryptedLogin) {
        this.encryptedLogin = encryptedLogin;
    }

    public void setEncryptedPass(byte[] encryptedPass) {
        this.encryptedPass = encryptedPass;
    }

    public void setEncryptedNote(byte[] encryptedNote) {
        this.encryptedNote = encryptedNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(id, password.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

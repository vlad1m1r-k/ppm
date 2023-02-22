package com.vladimir.ppm.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "passwords")
public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean deleted = false;
    private Date createdDate;
    private String createdBy;
    private Date editedDate;
    private String editedBy;
    private Date deletedDate;
    private String deletedBy;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LobHolder encryptedLogin;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LobHolder encryptedPass;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LobHolder encryptedNote;

    @ManyToOne
    private Container parent;

    public Password() {}

    public Password(String name, Container parent, byte[] encryptedLogin, byte[] encryptedPass, byte[] encryptedNote, String createdBy) {
        this.name = name;
        this.parent = parent;
        this.encryptedLogin = new LobHolder(encryptedLogin);
        this.encryptedPass = new LobHolder(encryptedPass);
        this.encryptedNote = new LobHolder(encryptedNote);
        this.createdBy = createdBy;
        this.createdDate = new Date();
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getEditedDate() {
        return editedDate;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public byte[] getEncryptedLogin() {
        return encryptedLogin.getData();
    }

    public byte[] getEncryptedPass() {
        return encryptedPass.getData();
    }

    public byte[] getEncryptedNote() {
        return encryptedNote.getData();
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

    public void setEditedDate(Date editedDate) {
        this.editedDate = editedDate;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public void setEncryptedLogin(byte[] encryptedLogin) {
        this.encryptedLogin.setData(encryptedLogin);
    }

    public void setEncryptedPass(byte[] encryptedPass) {
        this.encryptedPass.setData(encryptedPass);
    }

    public void setEncryptedNote(byte[] encryptedNote) {
        this.encryptedNote.setData(encryptedNote);
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

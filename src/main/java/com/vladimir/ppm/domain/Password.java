package com.vladimir.ppm.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] encryptedLogin;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] encryptedPass;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] encryptedNote;

    @ManyToOne
    private Container parent;

    public Password() {}

    public Password(String name, Container parent, String createdBy) {
        this.name = name;
        this.parent = parent;
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

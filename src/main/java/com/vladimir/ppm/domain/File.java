package com.vladimir.ppm.domain;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int size;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LobHolder encryptedBody;

    private Date createdDate;
    private String createdBy;
    private Date editedDate;
    private String editedBy;
    private Date deletedDate;
    private String deletedBy;
    private boolean deleted = false;

    @ManyToOne
    private Container parent;

    public File() {}

    public File(Container parent, String name, int size, byte[] encryptedBody, String createdBy) {
        this.parent = parent;
        this.name = name;
        this.size = size;
        this.encryptedBody = new LobHolder(encryptedBody);
        this.createdBy = createdBy;
        createdDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public byte[] getEncryptedBody() {
        return encryptedBody.getData();
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

    public boolean isDeleted() {
        return deleted;
    }

    public Container getParent() {
        return parent;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return id.equals(file.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

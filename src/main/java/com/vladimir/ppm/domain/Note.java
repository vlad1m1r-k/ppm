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
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private byte[] encryptedText;

    private boolean deleted = false;

    @ManyToOne
    private Container parent;

    public Note () {}

    public Note(Container parent, String name, byte[] encryptedText) {
        this.parent = parent;
        this.name = name;
        this.encryptedText = encryptedText;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getEncryptedText() {
        return encryptedText;
    }

    public Container getParent() {
        return parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEncryptedText(byte[] encryptedText) {
        this.encryptedText = encryptedText;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isDeleted() {
        return deleted;
    }
}

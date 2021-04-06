package com.vladimir.ppm.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "containers")
public class Container {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date createdDate;
    private String createdBy;
    private Date editedDate;
    private String editedBy;
    private Date deletedDate;
    private String deletedBy;
    private boolean deleted = false;

    @ManyToOne
    private Container parent;

    @OneToMany
    private Set<Container> children = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsRO = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsRW = new HashSet<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private Set<Password> passwords = new HashSet<>();

    public Container() {
    }

    public Container(String name, Container parent, String createdBy) {
        this.name = name;
        this.parent = parent;
        this.createdBy = createdBy;
        createdDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Container getParent() {
        return parent;
    }

    public Set<Group> getGroupsRO() {
        return groupsRO;
    }

    public Set<Group> getGroupsRW() {
        return groupsRW;
    }

    public Set<Container> getChildren() {
        return children;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public Set<Password> getPasswords() {
        return passwords;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public void setGroupsRO(Set<Group> groupsRO) {
        this.groupsRO = groupsRO;
    }

    public void setGroupsRW(Set<Group> groupsRW) {
        this.groupsRW = groupsRW;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void addChild(Container child) {
        child.setParent(this);
        children.add(child);
    }

    public void addGroupRO(Group group) {
        groupsRO.add(group);
    }

    public void addGroupRW(Group group) {
        groupsRW.add(group);
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void addPassword(Password password) {
        passwords.add(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return id.equals(container.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

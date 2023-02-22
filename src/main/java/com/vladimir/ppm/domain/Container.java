package com.vladimir.ppm.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private final Set<Container> children = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsNA = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsPT = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsRO = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsRW = new HashSet<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private final Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private final Set<Password> passwords = new HashSet<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private final Set<File> files = new HashSet<>();

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

    public Set<Group> getGroupsNA() {
        return groupsNA;
    }

    public Set<Group> getGroupsPT() {
        return groupsPT;
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

    public Set<File> getFiles() {
        return files;
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

    public void setGroupsNA(Set<Group> groupsNA) {
        this.groupsNA = groupsNA;
    }

    public void setGroupsPT(Set<Group> groupsPT) {
        this.groupsPT = groupsPT;
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

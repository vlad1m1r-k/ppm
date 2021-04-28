package com.vladimir.ppm.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sec_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean adminSettings = false;

    @ManyToMany(mappedBy = "groups")
    private final Set<User> users = new HashSet<>();

    public Group() {}

    public Group(String name, boolean adminSettings) {
        this.name = name;
        this.adminSettings = adminSettings;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAdminSettings() {
        return adminSettings;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdminSettings(boolean adminSettings) {
        this.adminSettings = adminSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

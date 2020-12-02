package com.vladimir.ppm.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @ManyToOne
    private Container parent;

    @OneToMany
    private Set<Container> children = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsRO = new HashSet<>();

    @ManyToMany
    private Set<Group> groupsRW = new HashSet<>();

    public Container() {}

    public Container(String name, Container parent) {
        this.name = name;
        this.parent = parent;
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

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public void addChild(Container child) {
        child.setParent(this);
        children.add(child);
    }

    public void addGroupRO (Group group) {
        groupsRO.add(group);
    }

    public void addGroupRW(Group group) {
        groupsRW.add(group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return name.equals(container.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

package com.vladimir.ppm.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
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
}

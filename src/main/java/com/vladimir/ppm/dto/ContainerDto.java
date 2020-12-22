package com.vladimir.ppm.dto;

import com.vladimir.ppm.domain.Access;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class ContainerDto implements Comparable<ContainerDto>{
    private final Long id;
    private final String name;
    private final Set<ContainerDto> children;
    private final List<NoteDto> notes;
    private final List<PasswordDto> passwords;
    private final Access access;

    private ContainerDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.children = builder.children;
        this.notes = builder.notes;
        this.passwords = builder.passwords;
        this.access = builder.access;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<ContainerDto> getChildren() {
        return children;
    }

    public List<NoteDto> getNotes() {
        return notes;
    }

    public List<PasswordDto> getPasswords() {
        return passwords;
    }

    public Access getAccess() {
        return access;
    }

    public String toJson() {
        return new JSONObject(this).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int compareTo(ContainerDto cntDto) {
        return this.name.compareTo(cntDto.getName());
    }

    public static class Builder {
        private Long id;
        private String name;
        private Set<ContainerDto> children;
        private List<NoteDto> notes;
        private List<PasswordDto> passwords;
        private Access access;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder children(Set<ContainerDto> children) {
            this.children = children;
            return this;
        }

        public Builder notes(List<NoteDto> notes) {
            this.notes = notes;
            return this;
        }

        public Builder passwords(List<PasswordDto> passwords) {
            this.passwords = passwords;
            return this;
        }

        public Builder access(Access access) {
            this.access = access;
            return this;
        }

        public ContainerDto build() {
            return new ContainerDto(this);
        }
    }
}

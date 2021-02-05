package com.vladimir.ppm.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimir.ppm.domain.Access;

import java.util.Date;
import java.util.List;

public class ContainerDto {
    private final Long id;
    private final String name;
    private final List<ContainerDto> children;
    private final List<NoteDto> notes;
    private final List<PasswordDto> passwords;
    private final Access access;
    private final String createdDate;
    private final String createdBy;
    private final String editedDate;
    private final String editedBy;
    private final String deletedDate;
    private final String deletedBy;

    private ContainerDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.children = builder.children;
        this.notes = builder.notes;
        this.passwords = builder.passwords;
        this.access = builder.access;
        this.createdDate = builder.createdDate;
        this.createdBy = builder.createdBy;
        this.editedDate = builder.editedDate;
        this.editedBy = builder.editedBy;
        this.deletedDate = builder.deletedDate;
        this.deletedBy = builder.deletedBy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ContainerDto> getChildren() {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getEditedDate() {
        return editedDate;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public String getDeletedDate() {
        return deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private List<ContainerDto> children;
        private List<NoteDto> notes;
        private List<PasswordDto> passwords;
        private Access access;
        private String createdDate;
        private String createdBy;
        private String editedDate;
        private String editedBy;
        private String deletedDate;
        private String deletedBy;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder children(List<ContainerDto> children) {
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

        public Builder createdDate(Date createdDate) {
            this.createdDate = String.format("%1$tF %1$tT", createdDate);
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder editedDate(Date editedDate) {
            this.editedDate = String.format("%1$tF %1$tT", editedDate);
            return this;
        }

        public Builder editedBy(String editedBy) {
            this.editedBy = editedBy;
            return this;
        }

        public Builder deletedDate(Date deletedDate) {
            this.deletedDate = String.format("%1$tF %1$tT", deletedDate);
            return this;
        }

        public Builder deletedBy(String deletedBy) {
            this.deletedBy = deletedBy;
            return this;
        }

        public ContainerDto build() {
            return new ContainerDto(this);
        }
    }
}

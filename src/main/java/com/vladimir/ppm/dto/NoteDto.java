package com.vladimir.ppm.dto;

import java.util.Date;

public class NoteDto {
    private final Long id;
    private final String name;
    private final String createdDate;
    private final String createdBy;
    private final String editedDate;
    private final String editedBy;
    private final String deletedDate;
    private final String deletedBy;

    private NoteDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
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

        public NoteDto build() {
            return new NoteDto(this);
        }
    }
}

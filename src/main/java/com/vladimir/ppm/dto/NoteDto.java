package com.vladimir.ppm.dto;

public class NoteDto implements Comparable<NoteDto> {
    private final Long id;
    private final String name;

    private NoteDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int compareTo(NoteDto noteDto) {
        return this.name.compareTo(noteDto.getName());
    }

    public static class Builder {
        private Long id;
        private String name;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public NoteDto build() {
            return new NoteDto(this);
        }
    }
}

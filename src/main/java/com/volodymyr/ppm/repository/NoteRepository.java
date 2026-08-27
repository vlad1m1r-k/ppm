package com.volodymyr.ppm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> getAllByDeleted(boolean deleted);
}

package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}

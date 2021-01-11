package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Set<Note> getAllByParentAndDeleted(Container parent, boolean deleted);
}

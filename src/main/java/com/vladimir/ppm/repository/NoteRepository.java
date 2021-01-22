package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Note;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> getAllByParentAndDeleted(Container parent, boolean deleted, Sort sort);
}

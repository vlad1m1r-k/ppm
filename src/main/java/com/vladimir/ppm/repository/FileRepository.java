package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.File;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> getAllByParentAndDeleted(Container parent, boolean deleted, Sort sort);
}

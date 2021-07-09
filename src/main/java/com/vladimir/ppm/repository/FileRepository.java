package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}

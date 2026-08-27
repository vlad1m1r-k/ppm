package com.volodymyr.ppm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.File;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> getAllByDeleted(boolean deleted);
}

package com.volodymyr.ppm.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.Container;
import com.volodymyr.ppm.domain.Password;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    List<Password> getAllByParentAndDeleted(Container parent, boolean deleted, Sort sort);
}

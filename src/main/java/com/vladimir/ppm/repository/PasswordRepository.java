package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Password;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    List<Password> getAllByParentAndDeleted(Container parent, boolean deleted, Sort sort);
}

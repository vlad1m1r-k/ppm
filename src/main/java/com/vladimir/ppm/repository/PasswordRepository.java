package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Password;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    Set<Password> getAllByParentAndDeleted(Container parent, boolean deleted);
}

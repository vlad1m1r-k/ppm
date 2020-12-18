package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, Long> {
}

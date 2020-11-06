package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByLogin(String login);
}

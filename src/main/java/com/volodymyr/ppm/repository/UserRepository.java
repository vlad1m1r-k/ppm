package com.volodymyr.ppm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByLogin(String login);
}

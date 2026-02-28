package com.volodymyr.ppm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupByName(String name);
}

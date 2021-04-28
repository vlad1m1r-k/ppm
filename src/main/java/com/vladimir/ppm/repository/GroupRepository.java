package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupByName(String name);
}

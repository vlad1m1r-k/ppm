package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
    Container getContainerByName(String name);
}

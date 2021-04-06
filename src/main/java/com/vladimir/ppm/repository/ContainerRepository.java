package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Container;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ContainerRepository extends JpaRepository<Container, Long> {
    Container getContainerByName(String name);
    List<Container> getAllByDeleted(boolean deleted, Sort sort);
    Set<Container> getContainersByParent(Container parent);
}

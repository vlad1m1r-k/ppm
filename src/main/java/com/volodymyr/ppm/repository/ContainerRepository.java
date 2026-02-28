package com.volodymyr.ppm.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.Container;

import java.util.List;
import java.util.Set;

public interface ContainerRepository extends JpaRepository<Container, Long> {
    Container getContainerByName(String name);
    List<Container> getAllByDeleted(boolean deleted, Sort sort);
    Set<Container> getContainersByParent(Container parent);
}

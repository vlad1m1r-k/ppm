package com.volodymyr.ppm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.UserAuthData;

public interface UserAtuthDataRepository extends JpaRepository<UserAuthData, Long>{

}

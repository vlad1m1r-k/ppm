package com.volodymyr.ppm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.PwdGenSettings;

public interface PwdGenSettingsRepository extends JpaRepository<PwdGenSettings, Long> {
}

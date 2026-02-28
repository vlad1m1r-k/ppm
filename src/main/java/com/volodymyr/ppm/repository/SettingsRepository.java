package com.volodymyr.ppm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volodymyr.ppm.domain.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
}

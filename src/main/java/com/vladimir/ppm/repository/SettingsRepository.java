package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
}

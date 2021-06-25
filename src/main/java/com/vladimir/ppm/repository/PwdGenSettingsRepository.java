package com.vladimir.ppm.repository;

import com.vladimir.ppm.domain.PwdGenSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PwdGenSettingsRepository extends JpaRepository<PwdGenSettings, Long> {
}

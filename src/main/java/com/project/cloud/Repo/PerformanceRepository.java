package com.project.cloud.Repo;

import com.project.cloud.model.PerformanceMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<PerformanceMetric, Long> {
}

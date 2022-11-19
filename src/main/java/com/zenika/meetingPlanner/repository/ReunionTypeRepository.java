package com.zenika.meetingPlanner.repository;

import com.zenika.meetingPlanner.model.ReunionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReunionTypeRepository extends JpaRepository<ReunionType, Long> {
}

package com.zenika.meetingPlanner.repository;

import com.zenika.meetingPlanner.model.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    List<Equipement> findEquipementsBySallesId(Long salleId);
}

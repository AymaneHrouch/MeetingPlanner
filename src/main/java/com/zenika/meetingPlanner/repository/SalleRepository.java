package com.zenika.meetingPlanner.repository;

import com.zenika.meetingPlanner.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {
    List<Salle> findAllByOrderByCapacite();
}

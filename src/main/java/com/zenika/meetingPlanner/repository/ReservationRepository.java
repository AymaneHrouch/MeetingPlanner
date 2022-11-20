package com.zenika.meetingPlanner.repository;

import com.zenika.meetingPlanner.model.Reservation;
import com.zenika.meetingPlanner.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    public List<Reservation> findReservationsBySalle(Salle salle);
}

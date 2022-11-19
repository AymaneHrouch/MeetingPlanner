package com.zenika.meetingPlanner.controller.v0;

import com.zenika.meetingPlanner.dto.ReservationDto;
import com.zenika.meetingPlanner.model.Reservation;
import com.zenika.meetingPlanner.repository.ReservationRepository;
import com.zenika.meetingPlanner.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "reservations")
@Slf4j
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationRepository reservationRepository, ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    @GetMapping
    ResponseEntity<List<Reservation>> getReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        if (reservations.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        }
    }

    @PostMapping
    ResponseEntity<Reservation> makeReservation(@RequestBody ReservationDto reservationDto) {
        Reservation reservation = reservationService.createReservation(reservationDto);
        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }
    }

}

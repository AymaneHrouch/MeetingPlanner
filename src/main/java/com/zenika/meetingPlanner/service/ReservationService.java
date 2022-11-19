package com.zenika.meetingPlanner.service;

import com.zenika.meetingPlanner.dto.ReservationDto;
import com.zenika.meetingPlanner.model.Equipement;
import com.zenika.meetingPlanner.model.Reservation;
import com.zenika.meetingPlanner.model.ReunionType;
import com.zenika.meetingPlanner.model.Salle;
import com.zenika.meetingPlanner.repository.ReservationRepository;
import com.zenika.meetingPlanner.repository.ReunionTypeRepository;
import com.zenika.meetingPlanner.repository.SalleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.zenika.meetingPlanner.constants.constants.MINUTES_FOR_CLEANING_BEFORE_MEETING;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SalleRepository salleRepository;
    private final ReunionTypeRepository reunionTypeRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, SalleRepository salleRepository, ReunionTypeRepository reunionTypeRepository) {
        this.reservationRepository = reservationRepository;
        this.salleRepository = salleRepository;
        this.reunionTypeRepository = reunionTypeRepository;
    }

    public Reservation createReservation(ReservationDto reservationDto) {
        Long reunionId = reservationDto.getRenuionTypeId();
        LocalDateTime dateDebut = reservationDto.getDateDebut();
        LocalDateTime dateFin = reservationDto.getDateFin();
        int nombrePersonnesConvie = reservationDto.getNombrePersonnesConvie();

        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalStateException("La date de fin doit être aprés la date de début");
        }

        if (dateDebut.getHour() < 8 || dateDebut.getHour() > 20 || dateFin.getHour() > 20) {
            throw new IllegalStateException("Les réunion doit être entre 8H et 20H");
        }
        ReunionType reunionType = reunionTypeRepository.findById(reunionId).get();
        List<Equipement> equipements = reunionType.getEquipementsRequis();

        List<Salle> salles = salleRepository.findAll().stream()
                .filter(getSallePredicate(nombrePersonnesConvie, reunionType, equipements))
                .collect(Collectors.toList());

        log.info("the reunion type is " + reunionType.getName());
        // It's better to reserve the salle with the least capacity (in order to leave bigger ones to potential bigger meetings)
        sortSallesByCapacite(salles);

        log.info("We could find " + salles.size() + " salles correspending to your search.");
        log.info("We now try to find which one isn't reserved in the given time interval");
        List<Reservation> allReservations = reservationRepository.findAll();
        boolean skipSalle = false;
        for (Salle salle : salles) {
            skipSalle = false;
            skipSalle = verifyIfSalleAlreadyReserved(dateDebut, dateFin, allReservations, skipSalle, salle);
            if (skipSalle) continue;

            // If we get here then the salle is good for reservation
            // We make a new reservation, save it to our repository then return it.
            Reservation reservation = new Reservation();
            reservation.setSalle(salle);
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setReunionType(reunionType);
            log.info(salle + " is a convenient salle");
            reservationRepository.save(reservation);
            return reservation;
        }

        // If we get here then we didn't find any eligible salles for our reservation.
        return null;
    }

    private boolean verifyIfSalleAlreadyReserved(LocalDateTime dateDebut, LocalDateTime dateFin, List<Reservation> allReservations, boolean skipSalle, Salle salle) {
        for (Reservation reservation : allReservations) {
            // A bad reservation is a reservation that is in the same salle
            // finishes after the beginning time (which is the actual beginning minus one hour)
            // starts before the ending time
            if (reservation.getSalle().equals(salle) &&
                    reservation.getDateFin().isAfter(dateDebut.minusMinutes(MINUTES_FOR_CLEANING_BEFORE_MEETING)) &&
                    reservation.getDateDebut().isBefore(dateFin)) {
                log.info("We found a conflict between reservations, this salle cannot be reserved");
                skipSalle = true;
                break;
            }
        }
        return skipSalle;
    }

    private void sortSallesByCapacite(List<Salle> salles) {
        Collections.sort(salles, new Comparator<Salle>() {
            @Override
            public int compare(Salle s1, Salle s2) {
                return s1.getCapaciteDisponible() - s2.getCapaciteDisponible();
            }
        });
    }

    private Predicate<Salle> getSallePredicate(int nombrePersonneConvie, ReunionType reunionType, List<Equipement> equipements) {
        return salle ->
                salle.getEquipements().containsAll(equipements) &&
                        salle.getCapaciteDisponible() >= reunionType.getMinCapaciteRequis() &&
                        salle.getCapaciteDisponible() >= nombrePersonneConvie;
    }

}

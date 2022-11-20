package com.zenika.meetingPlanner.service;

import com.zenika.meetingPlanner.config.StartupRunner;
import com.zenika.meetingPlanner.constants.constants;
import com.zenika.meetingPlanner.dto.ReservationDto;
import com.zenika.meetingPlanner.model.Reservation;
import com.zenika.meetingPlanner.model.Salle;
import com.zenika.meetingPlanner.repository.EquipementRepository;
import com.zenika.meetingPlanner.repository.ReservationRepository;
import com.zenika.meetingPlanner.repository.ReunionTypeRepository;
import com.zenika.meetingPlanner.repository.SalleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.zenika.meetingPlanner.constants.constants.END_HOUR;
import static com.zenika.meetingPlanner.constants.constants.START_HOUR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
public class ReservationServiceTest {

    private final EquipementRepository equipementRepository;
    private final SalleRepository salleRepository;
    private final ReunionTypeRepository reunionTypeRepository;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceTest(EquipementRepository equipementRepository, SalleRepository salleRepository, ReunionTypeRepository reunionTypeRepository, ReservationService reservationService, ReservationRepository reservationRepository) {
        this.equipementRepository = equipementRepository;
        this.salleRepository = salleRepository;
        this.reunionTypeRepository = reunionTypeRepository;
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
    }

    @BeforeEach
    void init() throws Exception {
        new StartupRunner(equipementRepository, salleRepository, reunionTypeRepository).run();
        log.info("startup");
    }

    @Test
    void shouldReturnReservationIfPossible() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, 10, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, 11, 0);

        // SPEC réunion
        Long reunionTypeId = 2L;

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(3);
        reservationDto.setReunionTypeId(reunionTypeId);

        Reservation returned = reservationService.createReservation(reservationDto);
        assertThat(returned).isInstanceOf(Reservation.class);
    }

    @Test
    void shouldReturnNullIfNoConvenientSalleExists() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, 10, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, 11, 0);

        // VC réunion
        Long reunionTypeId = 1L;

        // There is no salle that can hold 25 attendant
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(25);
        reservationDto.setReunionTypeId(reunionTypeId);

        Reservation returned = reservationService.createReservation(reservationDto);
        assertThat(returned).isNull();
    }

    @Test
    void shouldReturnNullIfAllConvenientSallesAreReserved() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, 10, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, 11, 0);

        Long reunionTypeId = 1L;

        // This request should want to reserve the salle 9 in the specified period
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(5);
        reservationDto.setReunionTypeId(reunionTypeId);

        // We make a reservation in the same salle with the same timestamps
        Reservation reservation = new Reservation();
        reservation.setReunionType(reunionTypeRepository.findById(reunionTypeId).get());
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        Salle salle = salleRepository.findById(9L).get();
        reservation.setSalle(salle);

        reservationRepository.save(reservation);

        Reservation returned = reservationService.createReservation(reservationDto);
        assertThat(returned).isNull();
    }

    @Test
    void shouldReturneNullIfConvenientSallesAreNotEmptyDuringTheHourBefore() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, 10, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, 11, 0);

        Long reunionTypeId = 1L;

        // This request should want to reserve the salle 9 from 10 to 11, so it should be empty from 9 to 11
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(5);
        reservationDto.setReunionTypeId(reunionTypeId);

        // We make a reservation in the same salle from 8 to 10, so it's not empty from 9 to 10
        LocalDateTime earlierReservationDateDebut = LocalDateTime.of(2022, 10, 10, 8, 0);
        LocalDateTime earlierReservationDateFin = LocalDateTime.of(2022, 10, 10, 10, 0);


        Reservation reservation = new Reservation();
        reservation.setReunionType(reunionTypeRepository.findById(reunionTypeId).get());
        reservation.setDateDebut(earlierReservationDateDebut);
        reservation.setDateFin(earlierReservationDateFin);
        Salle salle = salleRepository.findById(9L).get();
        reservation.setSalle(salle);

        reservationRepository.save(reservation);

        Reservation returned = reservationService.createReservation(reservationDto);
        assertThat(returned).isNull();
    }

    @Test
    void shouldThrowIfDateDebutIsBeforeStartHour() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, START_HOUR - 1, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, 11, 0);

        Long reunionTypeId = 1L;

        // This request should want to reserve the salle 7 to 11
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(5);
        reservationDto.setReunionTypeId(reunionTypeId);

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(reservationDto));
    }

    @Test
    void shouldThrowIfDateDebutIsAfterEndHour() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, END_HOUR + 1, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, END_HOUR + 2, 0);

        Long reunionTypeId = 1L;

        // This request should want to reserve the salle 9 to 21
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(5);
        reservationDto.setReunionTypeId(reunionTypeId);

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(reservationDto));
    }

    @Test
    void shouldThrowIfDateFinIsAfterEndHour() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, 9, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 11, END_HOUR + 1, 0);

        Long reunionTypeId = 1L;

        // This request should want to reserve the salle 9 to 21
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(5);
        reservationDto.setReunionTypeId(reunionTypeId);

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(reservationDto));
    }

    @Test
    void shouldThrowIfDateFinIsBeforeDateDebut() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, 12, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, 11, 0);

        Long reunionTypeId = 1L;

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(5);
        reservationDto.setReunionTypeId(reunionTypeId);

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(reservationDto));
    }

    @Test
    void shouldReserveSalleWithTheLowestCapacitePossible() {
        LocalDateTime dateDebut = LocalDateTime.of(2022, 10, 10, 10, 0);
        LocalDateTime dateFin = LocalDateTime.of(2022, 10, 10, 11, 0);

        // SPEC réunion
        Long reunionTypeId = 2L;

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDateDebut(dateDebut);
        reservationDto.setDateFin(dateFin);
        reservationDto.setNombrePersonnesConvie(2);
        reservationDto.setReunionTypeId(reunionTypeId);

        // There are two convenient salles for this,
        // * salle E1004 with a capacity of 4
        // * salle E2004 with a capacity of 9
        // --> We should reserve E1004

        Reservation returned = reservationService.createReservation(reservationDto);
        assertThat(returned.getSalle().getNom()).isEqualTo("e1004");
    }
}

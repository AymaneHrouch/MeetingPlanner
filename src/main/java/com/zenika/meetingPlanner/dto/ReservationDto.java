package com.zenika.meetingPlanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDto {
    private int nombrePersonnesConvie;
    private Long renuionTypeId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}

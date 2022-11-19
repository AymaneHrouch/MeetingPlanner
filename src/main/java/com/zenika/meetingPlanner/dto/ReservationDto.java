package com.zenika.meetingPlanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDto {
    private int nombrePersonnesConvie;
    private Long reunionTypeId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}

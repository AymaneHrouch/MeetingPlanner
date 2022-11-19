package com.zenika.meetingPlanner.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn
    private ReunionType reunionType;

    public Reservation(LocalDateTime dateDebut, LocalDateTime dateFin, Salle salle, ReunionType reunionType) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.salle = salle;
        this.reunionType = reunionType;
    }
}

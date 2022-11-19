package com.zenika.meetingPlanner.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static com.zenika.meetingPlanner.constants.constants.PERCENTAGE_CAPACITE_DISPONIBLE;

@Getter
@Setter
@Table(name = "salle")
@Entity
@NoArgsConstructor
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String nom;
    private int capacite;

    @OneToMany
    @JsonIgnore
    private List<Reservation> reservations;

    @Transient
    private int capaciteDisponible;

    @ManyToMany
    @JoinTable(name = "salles_equipements",
            joinColumns = {@JoinColumn(name = "salle_id")},
            inverseJoinColumns = {@JoinColumn(name = "equipement_id")})
    List<Equipement> equipements;

    public Salle(String nom, int capacite) {
        this.nom = nom;
        this.capacite = capacite;
    }

    public Salle(String nom, int capacite, List<Equipement> equipements) {
        this.nom = nom;
        this.capacite = capacite;
        this.equipements = equipements;
    }

    public int getCapaciteDisponible() {
        return (int) (capacite * PERCENTAGE_CAPACITE_DISPONIBLE);
    }

    @Override
    public String toString() {
        return "id = " + id + " nom = " + nom + " capacite = " + capacite;
    }
}

package com.zenika.meetingPlanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @ManyToMany(mappedBy = "equipements")
    @JsonIgnore
    List<Salle> salles;

    @ManyToMany(mappedBy = "equipementsRequis")
    @JsonIgnore
    List<ReunionType> reunionTypes;

    public Equipement(String nom) {
        this.nom = nom;
    }
}

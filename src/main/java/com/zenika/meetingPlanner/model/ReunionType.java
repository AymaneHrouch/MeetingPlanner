package com.zenika.meetingPlanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class ReunionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String longName;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "reunion_type_equipements_requis",
            joinColumns = {@JoinColumn(name = "reunion_type_id")},
            inverseJoinColumns = {@JoinColumn(name = "equipement_id")})
    @JsonIgnore
    private List<Equipement> equipementsRequis;

    private int MinCapaciteRequis;

    public ReunionType(String name, String longName, List<Equipement> equipementsRequis) {
        this.name = name;
        this.longName = longName;
        this.equipementsRequis = equipementsRequis;
    }

    public ReunionType(String name, String longName) {
        this.name = name;
        this.longName = longName;
    }

    @Override
    public String toString() {
        return "name = " + name;
    }
}

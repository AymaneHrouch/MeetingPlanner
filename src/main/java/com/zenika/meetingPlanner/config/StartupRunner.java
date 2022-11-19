package com.zenika.meetingPlanner.config;

import com.zenika.meetingPlanner.model.Equipement;
import com.zenika.meetingPlanner.model.ReunionType;
import com.zenika.meetingPlanner.model.Salle;
import com.zenika.meetingPlanner.repository.EquipementRepository;
import com.zenika.meetingPlanner.repository.ReunionTypeRepository;
import com.zenika.meetingPlanner.repository.SalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StartupRunner implements CommandLineRunner {

    private final EquipementRepository equipementRepository;
    private final SalleRepository salleRepository;
    private final ReunionTypeRepository reunionTypeRepository;

    @Autowired
    public StartupRunner(EquipementRepository equipementRepository, SalleRepository salleRepository, ReunionTypeRepository reunionTypeRepository) {
        this.equipementRepository = equipementRepository;
        this.salleRepository = salleRepository;
        this.reunionTypeRepository = reunionTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Création des équipements
        Equipement ecran = new Equipement("ECRAN");
        Equipement pieuvre = new Equipement("PIEUVRE");
        Equipement tableau = new Equipement("TABLEAU");
        Equipement webcam = new Equipement("WEBCAM");
        equipementRepository.saveAll(List.of(ecran, pieuvre, tableau, webcam));

        // Création des salles
        Salle e1001 = new Salle("e1001", 23);
        Salle e1002 = new Salle("e1002", 10, List.of(ecran));
        Salle e1003 = new Salle("e1003", 8, List.of(pieuvre));
        Salle e1004 = new Salle("e1004", 4, List.of(tableau));
        Salle e2001 = new Salle("e2001", 4);
        Salle e2002 = new Salle("e2002", 15, List.of(ecran, webcam));
        Salle e2003 = new Salle("e2003", 7);
        Salle e2004 = new Salle("e2004", 9, List.of(tableau));
        Salle e3001 = new Salle("e3001", 13, List.of(ecran, webcam, pieuvre));
        Salle e3002 = new Salle("e3002", 8);
        Salle e3003 = new Salle("e3003", 9, List.of(ecran, pieuvre));
        Salle e3004 = new Salle("e3004", 4);
        salleRepository.saveAll(List.of(e1001, e1002, e1003, e1004,
                e2001, e2002, e2003, e2004,
                e3001, e3002, e3003, e3004));

        // Création des types des réunions
        ReunionType vc = new ReunionType("VC", "Visioconférence",
                List.of(ecran, pieuvre, webcam));

        ReunionType spec = new ReunionType("SPEC", "Séance de partage et d'études de cas",
                List.of(tableau));

        ReunionType rs = new ReunionType("RS", "Réunion simple");
        rs.setMinCapaciteRequis(3);

        ReunionType rc = new ReunionType("RC", "Réunion couplée",
                List.of(tableau, ecran, pieuvre));

        reunionTypeRepository.saveAll(List.of(vc, spec, rs, rc));
    }
}

package com.zenika.meetingPlanner.controller.v0;

import com.zenika.meetingPlanner.model.Equipement;
import com.zenika.meetingPlanner.model.Salle;
import com.zenika.meetingPlanner.repository.EquipementRepository;
import com.zenika.meetingPlanner.repository.SalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


// This controller was added only for testing purposes
// As it wasn't a part of the assignment

@RestController
@RequestMapping(value = "salles")
public class SalleController {

    private final SalleRepository salleRepository;
    private final EquipementRepository equipementRepository;

    @Autowired
    public SalleController(SalleRepository salleRepository, EquipementRepository equipementRepository) {
        this.salleRepository = salleRepository;
        this.equipementRepository = equipementRepository;
    }

    @GetMapping
    public ResponseEntity<List<Salle>> getSalles() {
        return new ResponseEntity<>(salleRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = "{salleId}")
    public ResponseEntity<Salle> getSalle(@PathVariable("salleId") Long salleId) {
        return new ResponseEntity<>(salleRepository.findById(salleId).get(),
                HttpStatus.OK);
    }

    @GetMapping(path = "{salleId}/equipements")
    public ResponseEntity<List<Equipement>> getSallesEquipements(@PathVariable("salleId") Long salleId) {
        List<Equipement> equipements = equipementRepository.findEquipementsBySallesId(salleId);
        return new ResponseEntity<>(equipements, HttpStatus.OK);
    }

}

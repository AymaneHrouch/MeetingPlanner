# MeetingPlanner
Zenika assignment, an API using Spring Boot.

# Structure de projet
```
├── MeetingPlannerApplication.java
├── config
│   └── StartupRunner.java
├── constants
│   └── constants.java
├── controller
│   └── v0
│       ├── ReservationController.java
│       └── SalleController.java
├── dto
│   └── ReservationDto.java
├── model
│   ├── Equipement.java
│   ├── Reservation.java
│   ├── ReunionType.java
│   └── Salle.java
├── repository
│   ├── EquipementRepository.java
│   ├── ReservationRepository.java
│   ├── ReunionTypeRepository.java
│   └── SalleRepository.java
└── service
    └── ReservationService.java
```

## StartupRunner
Pour initialiser les données dans la base de données.

## constants
Il y a quatre contants qui nous donnent plus de flexibilité:
* `PERCENTAGE_CAPACITE_DISPONIBLE` pour définit le poucentage des personnes qui peuvent assister par rapport la capacité initiale. (ici: 70%)
* `MINUTES_FOR_CLEANING_BEFORE_MEETING` nombres de minutes consacré pour les agents des nettoyage. (ici: 60minutes)
* `START_HOUR` l'heure de départ (ici: 8)
* `END_HOUR` l'heure de fin (ici: 20)

## Reservation Service

Dans notre service, nous nous assurons d'abord que la période donnée est comprise entre l'heure de début et l'heure de fin.

Ensuite, nous obtenons les salles qui ont les équipements requis pour la réunion, et qui ont une capacité supérieure à la capacité requise pour notre réunion.

Ensuite, nous trions les salles en fonction de leur capacité, dans l'ordre croissant. La raison en est qu'il est toujours préférable de réserver la salle ayant la plus petite capacité possible, afin de garder les salles ayant une plus grande capacité pour d'autres.

nous itérons ensuite à travers ces salles et obtenons la première salle qui n'est pas réservée pendant la période spécifiée (y compris le temps de nettoyage).

Dans le cas où nous n'avons pas trouvé de salle correspondante, nous retournos CONFLICT Https response.

## Consommation de l'API

Pour utiliser ce service il suffit d'envoyer un POST request à l'endpoint `hostname:PORT/reservations` (par défault en local : `localhost:8080/reservations` )

sous format 

```json
{
    "dateDebut": "2022-11-10T06:00:00.000Z",
    "dateFin": "2022-11-10T12:00:00.000Z",
    "nombrePersonnesConvie": 8,
    "reunionTypeId": 2
}
```
### reunionTypeId

Voici les Ids des reunion types (pour le frontend il faut faire un mapping en utilisant une form)

![](https://user-images.githubusercontent.com/36491424/202863321-e73b2675-8f04-431c-830f-6d2df1723950.png)

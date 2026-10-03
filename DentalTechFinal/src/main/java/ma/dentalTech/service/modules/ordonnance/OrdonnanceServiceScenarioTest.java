package ma.dentalTech.service.modules.ordonnance;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.DbTestUtils;
import ma.dentalTech.repository.modules.medicament.impl.mySQL.MedicamentRepositoryImpl;
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.dentalTech.repository.modules.ordonnance.api.PrescriptionRepository;
import ma.dentalTech.repository.modules.ordonnance.impl.mySQL.OrdonnanceRepositoryImpl;
import ma.dentalTech.repository.modules.ordonnance.impl.mySQL.PrescriptionRepositoryImpl;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.service.modules.ordonnance.api.OrdonnanceService;
import ma.dentalTech.service.modules.ordonnance.impl.OrdonnanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//ROBAII IKRAM
class OrdonnanceServiceScenarioTest {

    private OrdonnanceService ordService;
    private OrdonnanceRepository ordRepo;
    private PrescriptionRepository prescRepo;

    // Repos auxiliaires pour préparer les données
    private PatientRepositoryImpl patientRepo;
    private MedicamentRepositoryImpl medRepo;

    @BeforeEach
    void setUp() {
        DbTestUtils.cleanDatabase();

        ordRepo = new OrdonnanceRepositoryImpl();
        prescRepo = new PrescriptionRepositoryImpl();
        patientRepo = new PatientRepositoryImpl();
        medRepo = new MedicamentRepositoryImpl();

        ordService = new OrdonnanceServiceImpl(ordRepo, prescRepo);
    }

    @Test
    void scenarioGestionOrdonnance() throws Exception {
        System.out.println("1. Préparation (Patient + Médicament)...");
        Patient p = Patient.builder().nom("Malak").prenom("S.").sexe(Sexe.FEMME).assurance(Assurance.Autre).build();
        patientRepo.create(p);

        Medicament m = Medicament.builder().nom("Doliprane").prixUnitaire(15.0).build();
        medRepo.create(m);

        System.out.println("2. Création Ordonnance Complexe...");
        Ordonnance ord = Ordonnance.builder()
                .date(LocalDate.now())
                .idPatient(p.getIdPatient())
                .idMedecin(1L)
                .build();

        List<Prescription> liste = new ArrayList<>();
        // Ajout d'une ligne de prescription
        liste.add(Prescription.builder()
                .idMct(m.getIdMct())
                .quantite(2)
                .frequence("3 fois par jour")
                .dureeEnJours(5)
                .build());

        // Action Service
        ordService.creerOrdonnanceComplete(ord, liste);

        // Vérification
        assertNotNull(ord.getIdOrd(), "L'ordonnance doit avoir un ID");
        List<Prescription> details = ordService.getDetailsOrdonnance(ord.getIdOrd());
        assertEquals(1, details.size(), "On doit retrouver 1 ligne de prescription");
        assertEquals("3 fois par jour", details.get(0).getFrequence());

        System.out.println("3. Suppression Ordonnance...");
        ordService.supprimerOrdonnance(ord.getIdOrd());

        // Vérif suppression en cascade
        assertNull(ordRepo.findById(ord.getIdOrd()));
        assertTrue(prescRepo.findByOrdonnanceId(ord.getIdOrd()).isEmpty());

        System.out.println(">>> SCÉNARIO ORDONNANCE RÉUSSI !");
    }
}
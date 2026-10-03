package ma.dentalTech.service.modules.rdvPatient;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.repository.DbTestUtils;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.repository.modules.rdvPatient.api.RDVRepository;
import ma.dentalTech.repository.modules.rdvPatient.impl.mySQL.RDVRepositoryImpl;
import ma.dentalTech.service.modules.rdvPatient.api.RDVService;
import ma.dentalTech.service.modules.rdvPatient.impl.RDVServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
//ROBAII IKRAM
class RDVServiceScenarioTest {

    private RDVService rdvService;
    private RDVRepository rdvRepo;
    private PatientRepositoryImpl patientRepo;

    @BeforeEach
    void setUp() {
        DbTestUtils.cleanDatabase();
        rdvRepo = new RDVRepositoryImpl();
        patientRepo = new PatientRepositoryImpl();
        rdvService = new RDVServiceImpl(rdvRepo);
    }

    @Test
    void scenarioCompletGestionRDV() throws Exception {
        // --- ETAPE 1 : Patient ---
        System.out.println("1. Création du Patient...");
        Patient patient = Patient.builder().nom("Dupont").prenom("Jean").sexe(Sexe.HOMME).assurance(Assurance.CNSS).build();
        patientRepo.create(patient);

        // --- ETAPE 2 : Planification ---
        System.out.println("2. Planification d'un RDV...");


        LocalDate dateTest = LocalDate.now().plusDays(20);
        LocalTime heureTest = LocalTime.of(23, 0);

        RDV rdv1 = RDV.builder()
                .date(dateTest)
                .heure(heureTest)
                .idPatient(patient.getIdPatient())
                .idMedecin(1L)
                .motif("Consultation")
                .build();

        rdvService.planifierRDV(rdv1);
        assertEquals(StatutRDV.PLANIFIE, rdv1.getStatut());

        // --- ETAPE 3 : Conflit ---
        System.out.println("3. Test du conflit d'horaire...");
        RDV rdvConflit = RDV.builder()
                .date(dateTest)   // On utilise la même date "lointaine"
                .heure(heureTest) // On utilise la même heure
                .idPatient(patient.getIdPatient())
                .idMedecin(1L)
                .build();

        assertThrows(ServiceException.class, () -> rdvService.planifierRDV(rdvConflit));

        // --- ETAPE 6 (NOUVEAU) : Confirmation ---
        System.out.println("6. Confirmation du RDV...");
        rdvService.confirmerRDV(rdv1.getIdRDV());
        RDV rdvConfirme = rdvRepo.findById(rdv1.getIdRDV());
        assertEquals(StatutRDV.CONFIRME, rdvConfirme.getStatut());

        // --- ETAPE 7 (NOUVEAU) : Modification (Déplacer le RDV) ---
        System.out.println("7. Modification du RDV (Changement heure)...");
        rdv1.setHeure(LocalTime.of(16, 0)); // On déplace de 14h à 16h
        rdvService.modifierRDV(rdv1);

        RDV rdvModifie = rdvRepo.findById(rdv1.getIdRDV());
        assertEquals(LocalTime.of(16, 0), rdvModifie.getHeure());

        // --- ETAPE 4 : Annulation ---
        System.out.println("4. Annulation du RDV...");
        rdvService.annulerRDV(rdv1.getIdRDV());
        RDV rdvAnnule = rdvRepo.findById(rdv1.getIdRDV());
        assertEquals(StatutRDV.ANNULE, rdvAnnule.getStatut());

        System.out.println(">>> SCÉNARIO COMPLET (Planif + Confirm + Modif + Annul) RÉUSSI !");
    }
}
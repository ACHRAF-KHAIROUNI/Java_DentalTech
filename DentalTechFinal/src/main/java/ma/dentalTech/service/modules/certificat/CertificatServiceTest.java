package ma.dentalTech.service.modules.certificat;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.service.modules.certificat.api.CertificatService;
import ma.dentalTech.service.modules.certificat.impl.CertificatServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CertificatServiceTest {

    private final CertificatService certificatService = new CertificatServiceImpl();
    private final PatientRepository patientRepo = new PatientRepositoryImpl();

    @Test
    void testCycleDeVieCertificat() throws ServiceException {
        // 1. PREPARATION : Création d'un patient temporaire pour le test
        Patient p = Patient.builder()
                .nom("TEST_CERTIF_NOM")
                .prenom("TEST_CERTIF_PRENOM")
                .sexe(Sexe.HOMME)
                .assurance(Assurance.Aucune)
                .build();
        patientRepo.create(p);
        Long idPatient = p.getIdPatient();
        Long idCertif = null;

        try {
            // 2. TEST : Générer Certificat
            Certificat c = Certificat.builder()
                    .idPatient(idPatient)
                    .duree(5) // 5 jours
                    .dateDebut(LocalDate.now())
                    .noteMedecin("Extraction dent de sagesse")
                    .idMedecin(1L)
                    .build();

            certificatService.genererCertificat(c);
            idCertif = c.getIdCertif();

            assertNotNull(idCertif, "Le certificat doit avoir un ID");
            // Vérification calcul date fin (Aujourd'hui + 5 jours)
            assertEquals(LocalDate.now().plusDays(5), c.getDateFin(), "La date de fin doit être calculée auto");

            // 3. TEST : Consulter (Nouvelle fonction)
            Certificat consulte = certificatService.consulterCertificat(idCertif);
            assertEquals("Extraction dent de sagesse", consulte.getNoteMedecin());

            // 4. TEST : Corriger (Modification durée)
            certificatService.corrigerCertificat(idCertif, 10, "Complications"); // On passe à 10 jours
            Certificat corrige = certificatService.consulterCertificat(idCertif);

            assertEquals(10, corrige.getDuree());
            assertEquals(LocalDate.now().plusDays(10), corrige.getDateFin(), "La date de fin doit être mise à jour après correction");

            // 5. TEST : Vérifier état maladie
            boolean enArret = certificatService.isPatientEnArretMaladie(idPatient);
            assertTrue(enArret, "Le patient devrait être considéré en arrêt maladie aujourd'hui");

            // 6. TEST : Aperçu impression
            String apercu = certificatService.apercuImpression(idCertif);
            assertTrue(apercu.contains("TEST_CERTIF_NOM"), "L'aperçu doit contenir le nom du patient");

        } finally {
            // 7. NETTOYAGE (Ordre inverse)
            if (idCertif != null) {
                try {
                    certificatService.supprimerCertificat(idCertif);
                } catch (Exception e) {
                    System.err.println("Erreur nettoyage certif: " + e.getMessage());
                }
            }
            if (idPatient != null) {
                try {
                    patientRepo.deleteById(idPatient);
                } catch (Exception e) {
                    System.err.println("Erreur nettoyage patient: " + e.getMessage());
                }
            }
        }
    }

    @Test
    void testChevauchementInterdit() throws ServiceException {
        // Patient temporaire
        Patient p = Patient.builder().nom("TEST_CHEV").prenom("TEST").sexe(Sexe.FEMME).assurance(Assurance.Aucune).build();
        patientRepo.create(p);
        Long idP = p.getIdPatient();
        Long idC1 = null;

        try {
            // Certif 1 : Du 1er au 10 du mois prochain
            LocalDate debut = LocalDate.now().plusMonths(1).withDayOfMonth(1);
            Certificat c1 = Certificat.builder().idPatient(idP).dateDebut(debut).duree(10).idMedecin(1L).build();
            certificatService.genererCertificat(c1);
            idC1 = c1.getIdCertif();

            // Certif 2 : Essaie de créer du 5 au 15 (Chevauchement !)
            LocalDate debut2 = debut.plusDays(4); // Le 5 du mois
            Certificat c2 = Certificat.builder().idPatient(idP).dateDebut(debut2).duree(10).idMedecin(1L).build();

            assertThrows(ServiceException.class, () -> {
                certificatService.genererCertificat(c2);
            }, "Devrait interdire le chevauchement");

        } finally {
            if (idC1 != null) certificatService.supprimerCertificat(idC1);
            if (idP != null) patientRepo.deleteById(idP);
        }
    }
}
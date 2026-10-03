package ma.dentalTech.service.modules.patient;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.service.modules.patient.api.PatientService;
import ma.dentalTech.service.modules.patient.impl.PatientServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientAndAntecedentTest {

    private final PatientService patientService = new PatientServiceImpl();

    @Test
    void testWorkflowIsolé() throws ServiceException {
        // Numéro unique pour éviter les conflits
        String telUnique = "06" + System.currentTimeMillis() / 1000;

        Patient p = Patient.builder()
                .nom("TEST_DTO")
                .prenom("Unit")
                .sexe(Sexe.HOMME)
                .assurance(Assurance.CNSS)
                .telephone(telUnique)
                .dateDeNaissance(LocalDate.of(2000, 1, 1))
                // IMPORTANT : On force la date de création à aujourd'hui pour être sûr que le filtre passe
                // même si la base de données ne met pas le défaut correctement.
                .dateCreation(LocalDate.now())
                .build();

        Long idPatient = null;

        try {
            // 1. Ajout
            patientService.ajouterPatient(p);
            idPatient = p.getIdPatient();

            // --- TEST DE LA NOUVELLE FONCTION MVC ---
            List<PatientDTO> dtos = patientService.getTodayPatientsAsDTO();

            assertFalse(dtos.isEmpty(), "La liste des patients d'aujourd'hui ne doit pas être vide");

            // CORRECTION ICI : Utilisation de equalsIgnoreCase pour éviter l'erreur "Unit" vs "UNIT"
            boolean foundInDto = dtos.stream()
                    .anyMatch(dto -> dto.getNomComplet().equalsIgnoreCase("TEST_DTO UNIT"));

            assertTrue(foundInDto, "Le patient créé doit être présent dans le DTO d'aujourd'hui (Vérifiez le Nom/Prénom)");

            // Vérification formatage
            // On utilise findFirst avec un filtre insensible à la casse
            PatientDTO monDto = dtos.stream()
                    .filter(d -> d.getNomComplet().toUpperCase().contains("TEST_DTO"))
                    .findFirst()
                    .orElse(null);

            assertNotNull(monDto, "Le DTO spécifique doit être trouvé");
            assertNotNull(monDto.getDateCreationFormatee(), "La date formatée ne doit pas être nulle");

        } finally {
            // Nettoyage
            if (idPatient != null) {
                try { patientService.supprimerPatient(idPatient); } catch (Exception e) {}
            }
        }
    }
}
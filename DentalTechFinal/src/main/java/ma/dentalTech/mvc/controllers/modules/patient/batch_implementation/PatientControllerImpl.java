package ma.dentalTech.mvc.controllers.modules.patient.batch_implementation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.service.modules.patient.api.PatientService;
import ma.dentalTech.service.modules.patient.impl.PatientServiceImpl;

@Data @AllArgsConstructor @NoArgsConstructor
public class PatientControllerImpl implements PatientController {

    private PatientService service = new PatientServiceImpl();

    @Override
    public void showRecentPatients() {
        System.out.println("--- BATCH: Patients récents ---");
        List<PatientDTO> dtos = service.getTodayPatientsAsDTO();
        if (dtos.isEmpty()) {
            System.out.println("Aucun patient aujourd'hui.");
        } else {
            dtos.forEach(d -> System.out.println("- " + d.getNomComplet()));
        }
    }

    @Override
    public void openManagementWindow() {
        System.out.println(">>> MODE BATCH ACTIVÉ : Gestion des patients via console.");
    }

    @Override
    public void savePatient(Patient patient) {
        try {
            service.ajouterPatient(patient);
            System.out.println("[SUCCESS] Patient ajouté : " + patient.getNom());
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public void updatePatient(Patient patient) {
        try {
            service.modifierPatient(patient);
            System.out.println("[SUCCESS] Patient modifié ID: " + patient.getIdPatient());
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public void deletePatient(Long idPatient) {
        try {
            service.supprimerPatient(idPatient);
            System.out.println("[SUCCESS] Patient supprimé ID: " + idPatient);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public List<Patient> getAllPatients() {
        try {
            return service.getAllPatients();
        } catch (Exception e) {
            System.err.println("[ERROR] Impossible de récupérer la liste : " + e.getMessage());
            return List.of(); // Retourne une liste vide en cas d'erreur
        }
    }

    @Override
    public List<Patient> searchPatients(String keyword) {
        try {
            return service.rechercherPatients(keyword);
        } catch (Exception e) {
            return List.of();
        }
    }
}
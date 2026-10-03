package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.entities.patient.Patient;
import java.util.List;

public interface PatientController {

    // --- Navigation / Display ---
    void showRecentPatients();      // Ancienne méthode (gardée pour compatibilité)
    void openManagementWindow();    // Nouvelle méthode (Ouvre le CRUD)

    // --- CRUD Actions (Called by View) ---
    void savePatient(Patient patient);
    void updatePatient(Patient patient);
    void deletePatient(Long idPatient);

    // --- Data Retrieval (Called by View) ---
    List<Patient> getAllPatients();
    List<Patient> searchPatients(String keyword);
}
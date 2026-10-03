package ma.dentalTech.mvc.controllers.modules.ordonnance.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.entities.patient.Patient;

import java.util.List;
//ROBAII IKRAM
public interface OrdonnanceController {

    // --- Chargement des données ---
    List<Ordonnance> chargerHistoriquePatient(Long idPatient);

    // Pour remplir les combobox du formulaire
    List<Patient> getTousLesPatients();
    List<Medicament> getTousLesMedicaments();

    // --- Actions ---
    void enregistrerOrdonnance(Ordonnance ord, List<Prescription> prescriptions) throws ServiceException, ValidationException;

    void supprimerOrdonnance(Long idOrd) throws ServiceException;

    void imprimerOrdonnance(Long idOrd);

    Ordonnance getOrdonnanceById(Long id);
    Patient getPatientById(Long id);
    List<Prescription> getDetailsOrdonnance(Long idOrdonnance);
}
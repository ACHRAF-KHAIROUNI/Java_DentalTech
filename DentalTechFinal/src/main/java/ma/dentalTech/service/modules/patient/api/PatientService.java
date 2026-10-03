package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.PatientDTO; // <--- Import du DTO

import java.util.List;

public interface PatientService {


    Patient ajouterPatient(Patient patient) throws ServiceException;

    Patient modifierPatient(Patient patient) throws ServiceException;

    void supprimerPatient(Long idPatient) throws ServiceException;

    Patient getPatientById(Long id) throws ServiceException;

    List<Patient> getAllPatients() throws ServiceException;

    List<Patient> rechercherPatients(String motCle) throws ServiceException;

    boolean patientExiste(String telephone) throws ServiceException;



    int calculerAge(Long idPatient) throws ServiceException;

    String getResumePatient(Long idPatient) throws ServiceException;


    // --- MVC
    List<PatientDTO> getTodayPatientsAsDTO();
}
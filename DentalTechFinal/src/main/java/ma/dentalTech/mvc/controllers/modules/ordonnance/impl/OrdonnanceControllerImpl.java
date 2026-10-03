package ma.dentalTech.mvc.controllers.modules.ordonnance.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.ordonnance.api.OrdonnanceController;
import ma.dentalTech.repository.modules.medicament.api.MedicamentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.ordonnance.api.OrdonnanceService;

import java.util.List;
//ROBAII IKRAM

public class OrdonnanceControllerImpl implements OrdonnanceController {

    private final OrdonnanceService ordService;

    // Repositories pour les listes déroulantes (Lecture seule)
    // Idéalement injectés, ici instanciés pour simplifier l'intégration
    private final PatientRepository patientRepo = new ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl();
    private final MedicamentRepository medRepo = new ma.dentalTech.repository.modules.medicament.impl.mySQL.MedicamentRepositoryImpl();

    // Injection du Service via le constructeur
    public OrdonnanceControllerImpl(OrdonnanceService ordService) {
        this.ordService = ordService;
    }

    @Override
    public List<Ordonnance> chargerHistoriquePatient(Long idPatient) {
        if (idPatient == null) return List.of();
        return ordService.getHistoriquePatient(idPatient);
    }

    @Override
    public List<Patient> getTousLesPatients() {
        return patientRepo.findAll();
    }

    @Override
    public List<Medicament> getTousLesMedicaments() {
        return medRepo.findAll();
    }

    @Override
    public void enregistrerOrdonnance(Ordonnance ord, List<Prescription> prescriptions) throws ServiceException, ValidationException {
        // Délégation au service métier
        ordService.creerOrdonnanceComplete(ord, prescriptions);
    }

    @Override
    public void supprimerOrdonnance(Long idOrd) throws ServiceException {
        ordService.supprimerOrdonnance(idOrd);
    }

    @Override
    public void imprimerOrdonnance(Long idOrd) {
        // Simulation d'impression
        System.out.println("🖨️ Impression lancée pour l'ordonnance #" + idOrd);
    }

    @Override
    public Ordonnance getOrdonnanceById(Long id) {
        return ordService.getOrdonnanceById(id);
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepo.findById(id);
    }

    @Override
    public List<Prescription> getDetailsOrdonnance(Long idOrdonnance) {
        return ordService.getDetailsOrdonnance(idOrdonnance);
    }
}
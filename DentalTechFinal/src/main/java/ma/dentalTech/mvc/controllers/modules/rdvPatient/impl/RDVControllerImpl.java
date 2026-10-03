package ma.dentalTech.mvc.controllers.modules.rdvPatient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.mvc.controllers.modules.rdvPatient.api.RDVController;
import ma.dentalTech.mvc.dto.RDVDTO;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.service.modules.rdvPatient.api.RDVService;

import java.time.LocalDate;
import java.util.List;

//ROBAII IKRAM

public class RDVControllerImpl implements RDVController {

    private final RDVService rdvService;
    private final PatientRepositoryImpl patientRepo = new PatientRepositoryImpl(); // Pour la liste déroulante

    private final ma.dentalTech.repository.modules.auth.MedecinRepo medecinRepo = new ma.dentalTech.repository.modules.auth.MedecinRepo();

    public RDVControllerImpl(RDVService rdvService) {
        this.rdvService = rdvService;
    }

    @Override
    public List<RDVDTO> chargerAgenda(LocalDate date) {
        // Appelle le service pour obtenir les RDV d'un jour précis
        // Si date est null, on prend aujourd'hui
        return rdvService.getAgendaDuJour(date != null ? date : LocalDate.now());
    }

    @Override
    public List<RDVDTO> chargerAgendaMensuel(int annee, int mois) {
        // Appelle le service pour obtenir tous les RDV du mois (pour colorier le
        // calendrier)
        return rdvService.getAgendaMensuel(annee, mois);
    }

    @Override
    public List<RDVDTO> chargerHistoriquePatient(Long idPatient) {
        return rdvService.getHistoriquePatient(idPatient);
    }

    @Override
    public RDV getRDVById(Long id) {
        return rdvService.getRDVById(id);
    }

    @Override
    public List<Patient> getTousLesPatients() {
        return patientRepo.findAll();
    }

    @Override
    public void enregistrerNouveauRDV(RDV rdv) throws ValidationException, ServiceException {
        rdvService.planifierRDV(rdv);
    }

    @Override
    public void modifierRDV(RDV rdv) throws ValidationException, ServiceException {
        rdvService.modifierRDV(rdv);
    }

    @Override
    public void annulerRDV(Long idRDV) throws ServiceException {
        rdvService.annulerRDV(idRDV);
    }

    @Override
    public void confirmerRDV(Long idRDV) throws ServiceException {
        rdvService.confirmerRDV(idRDV);
    }

    // Ajoute cette méthode (et déclare-la dans l'interface RDVController)
    @Override
    public List<ma.dentalTech.entities.userManager.Medecin> getTousLesMedecins() {
        return medecinRepo.findAll();
    }
}
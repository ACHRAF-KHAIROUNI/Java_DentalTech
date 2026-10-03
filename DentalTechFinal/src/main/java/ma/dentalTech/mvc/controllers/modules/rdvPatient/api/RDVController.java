package ma.dentalTech.mvc.controllers.modules.rdvPatient.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.mvc.dto.RDVDTO;

import java.time.LocalDate;
import java.util.List;

//ROBAII IKRAM

public interface RDVController {

    // --- LECTURE ---

    // Vue Micro (Détail d'une journée : 08h00, 08h30...)
    List<RDVDTO> chargerAgenda(LocalDate date);

    // Vue Macro (Calendrier complet du mois pour colorier les cases)
    List<RDVDTO> chargerAgendaMensuel(int annee, int mois);

    // Historique patient
    List<RDVDTO> chargerHistoriquePatient(Long idPatient);

    // Pour les formulaires
    RDV getRDVById(Long id);
    List<Patient> getTousLesPatients();

    // --- ECRITURE (Actions) ---

    void enregistrerNouveauRDV(RDV rdv) throws ValidationException, ServiceException;

    void modifierRDV(RDV rdv) throws ValidationException, ServiceException;

    void annulerRDV(Long idRDV) throws ServiceException;

    void confirmerRDV(Long idRDV) throws ServiceException;


    List<ma.dentalTech.entities.userManager.Medecin> getTousLesMedecins();


}
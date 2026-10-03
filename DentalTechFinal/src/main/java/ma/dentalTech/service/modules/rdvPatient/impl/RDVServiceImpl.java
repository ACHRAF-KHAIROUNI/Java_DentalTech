package ma.dentalTech.service.modules.rdvPatient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.common.validation.Validators;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.mvc.dto.RDVDTO;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.repository.modules.rdvPatient.api.RDVRepository;
import ma.dentalTech.service.common.NotificationService;
import ma.dentalTech.service.common.NotificationServiceImpl;
import ma.dentalTech.service.modules.rdvPatient.api.RDVService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RDVServiceImpl implements RDVService {

    private final RDVRepository rdvRepo;

    // Repositories auxiliaires (Lecture seule pour récupérer infos)
    private final PatientRepositoryImpl patientRepo = new PatientRepositoryImpl();

    // Service de Notification (Simulation Email)
    private final NotificationService notificationService = new NotificationServiceImpl();

    // Injection du Repository principal via le constructeur
    public RDVServiceImpl(RDVRepository rdvRepo) {
        this.rdvRepo = rdvRepo;
    }

    // =========================================================================
    // 1. GESTION DU FLUX RDV (Planifier, Modifier, Confirmer, Annuler)
    // =========================================================================

    @Override
    public void planifierRDV(RDV rdv) throws ServiceException, ValidationException {
        // 1. Validation des champs obligatoires
        validerDonneesRDV(rdv);

        // 2. Vérification Disponibilité (idRdvExclu = null car c'est une création)
        verifierDisponibilite(rdv.getIdMedecin(), rdv.getDate(), rdv.getHeure(), null);

        // 3. Persistance
        rdv.setStatut(StatutRDV.PLANIFIE);
        rdvRepo.create(rdv);

        // 4. Notification Email
        envoyerNotification(rdv.getIdPatient(), "Nouveau RDV",
                "Votre RDV est planifié pour le " + rdv.getDate() + " à " + rdv.getHeure());
    }

    @Override
    public void modifierRDV(RDV rdvModifie) throws ServiceException, ValidationException {
        // 1. Vérifier existence
        RDV rdvExistant = rdvRepo.findById(rdvModifie.getIdRDV());
        if (rdvExistant == null) {
            throw new ServiceException("Le rendez-vous à modifier n'existe pas.");
        }

        // 2. Validation
        validerDonneesRDV(rdvModifie);

        // 3. Vérifier Disponibilité SI l'horaire change
        // On exclut ce RDV de la vérification pour qu'il ne se bloque pas lui-même
        boolean horaireChange = !rdvExistant.getDate().equals(rdvModifie.getDate())
                || !rdvExistant.getHeure().equals(rdvModifie.getHeure());

        if (horaireChange) {
            verifierDisponibilite(rdvModifie.getIdMedecin(), rdvModifie.getDate(), rdvModifie.getHeure(),
                    rdvModifie.getIdRDV());
        }

        // 4. Mise à jour
        rdvRepo.update(rdvModifie);

        // 5. Notification
        if (horaireChange) {
            envoyerNotification(rdvModifie.getIdPatient(), "Modification RDV",
                    "Votre RDV a été déplacé au " + rdvModifie.getDate() + " à " + rdvModifie.getHeure());
        }
    }

    @Override
    public void confirmerRDV(Long idRDV) throws ServiceException {
        RDV rdv = rdvRepo.findById(idRDV);
        if (rdv == null)
            throw new ServiceException("RDV introuvable.");

        if (rdv.getStatut() == StatutRDV.ANNULE) {
            throw new ServiceException("Impossible de confirmer un RDV annulé.");
        }

        rdv.setStatut(StatutRDV.CONFIRME);
        rdvRepo.update(rdv);

        // Notification
        envoyerNotification(rdv.getIdPatient(), "Confirmation RDV",
                "Votre rendez-vous du " + rdv.getDate() + " est confirmé.");
    }

    @Override
    public void annulerRDV(Long idRDV) throws ServiceException {
        RDV rdv = rdvRepo.findById(idRDV);
        if (rdv == null)
            throw new ServiceException("RDV introuvable.");

        rdv.setStatut(StatutRDV.ANNULE);
        rdvRepo.update(rdv);

        // Notification
        envoyerNotification(rdv.getIdPatient(), "Annulation RDV",
                "Votre rendez-vous du " + rdv.getDate() + " a été annulé.");
    }

    // =========================================================================
    // 2. CONSULTATION (Agenda & Historique)
    // =========================================================================

    @Override
    public List<RDVDTO> getAgendaDuJour(LocalDate date) {
        List<RDV> entities = rdvRepo.findByDate(date);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<RDVDTO> getHistoriquePatient(Long idPatient) {
        List<RDV> entities = rdvRepo.findByPatientId(idPatient);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public RDV getRDVById(Long id) {
        return rdvRepo.findById(id);
    }

    @Override
    public Arrays getAllRDVs() {
        return null;
    }

    @Override
    public List<RDVDTO> getAgendaMensuel(int annee, int mois) {
        LocalDate debut = LocalDate.of(annee, mois, 1);
        LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());
        return rdvRepo.findByDateBetween(debut, fin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // 3. MÉTHODES PRIVÉES (Helpers)
    // =========================================================================

    private void validerDonneesRDV(RDV rdv) throws ValidationException {
        Validators.checkNotNull(rdv.getIdPatient(), "Le patient est obligatoire");
        Validators.checkNotNull(rdv.getIdMedecin(), "Le médecin est obligatoire");
        Validators.checkNotNull(rdv.getDate(), "La date est obligatoire");
        Validators.checkNotNull(rdv.getHeure(), "L'heure est obligatoire");
        Validators.checkDateFutureOrToday(rdv.getDate(), "Impossible de planifier dans le passé");
    }

    /**
     * Vérifie si un médecin est libre.
     * 
     * @param idRdvExclu : ID du RDV actuel (utilisé lors de la modification pour
     *                   ignorer le créneau actuel)
     */
    private void verifierDisponibilite(Long idMedecin, LocalDate date, LocalTime heure, Long idRdvExclu)
            throws ServiceException {
        List<RDV> rdvsDuJour = rdvRepo.findByDate(date);

        boolean conflit = rdvsDuJour.stream()
                .filter(r -> r.getIdMedecin() != null && r.getIdMedecin().equals(idMedecin)) // Sécurisé contre NPE
                .filter(r -> !StatutRDV.ANNULE.equals(r.getStatut())) // Sécurisé + StatutRDV enum compare safe
                .filter(r -> idRdvExclu == null || !r.getIdRDV().equals(idRdvExclu)) // if idRdvExclu is null, we can
                                                                                     // skip compare or just safe check
                .anyMatch(r -> r.getHeure().equals(heure)); // Même heure pile

        if (conflit) {
            throw new ServiceException("Le médecin n'est pas disponible à cet horaire.");
        }
    }

    private RDVDTO convertToDTO(RDV r) {
        Patient p = patientRepo.findById(r.getIdPatient());
        String nomP = (p != null) ? p.getNom() + " " + p.getPrenom() : "Patient Inconnu (" + r.getIdPatient() + ")";

        return RDVDTO.builder()
                .idRDV(r.getIdRDV())
                .date(r.getDate())
                .heure(r.getHeure())
                .motif(r.getMotif())
                .statut(r.getStatut() != null ? r.getStatut().name() : "")
                .nomPatient(nomP)
                .nomMedecin("Dr. ID " + r.getIdMedecin()) // Idéalement join avec MedecinRepo
                .build();
    }

    private void envoyerNotification(Long idPatient, String sujet, String message) {
        try {
            Patient p = patientRepo.findById(idPatient);
            String nom = (p != null) ? p.getNom() + " " + p.getPrenom() : "Patient";
            notificationService.envoyerEmail(nom, sujet, message);
        } catch (Exception e) {
            // On ne bloque pas le processus si l'envoi de mail échoue (c'est du bonus)
            System.err.println("Erreur envoi notification : " + e.getMessage());
        }
    }

}
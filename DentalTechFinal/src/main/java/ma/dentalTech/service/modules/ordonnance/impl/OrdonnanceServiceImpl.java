package ma.dentalTech.service.modules.ordonnance.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.common.validation.Validators;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.dentalTech.repository.modules.ordonnance.api.PrescriptionRepository;

import java.time.LocalDate;
import java.util.List;

public class OrdonnanceServiceImpl implements ma.dentalTech.service.modules.ordonnance.api.OrdonnanceService {

    private final OrdonnanceRepository ordRepo;
    private final PrescriptionRepository prescRepo;

    // Injection via constructeur
    public OrdonnanceServiceImpl(OrdonnanceRepository ordRepo, PrescriptionRepository prescRepo) {
        this.ordRepo = ordRepo;
        this.prescRepo = prescRepo;
    }

    @Override
    public void creerOrdonnanceComplete(Ordonnance ord, List<Prescription> prescriptions) throws ServiceException, ValidationException {
        // 1. Validations
        Validators.checkNotNull(ord.getIdPatient(), "Le patient est obligatoire");
        Validators.checkNotNull(ord.getIdMedecin(), "Le médecin est obligatoire");

        if (prescriptions == null || prescriptions.isEmpty()) {
            throw new ValidationException("Impossible de créer une ordonnance vide (sans médicaments).");
        }

        // 2. Création de l'en-tête Ordonnance
        if (ord.getDate() == null) ord.setDate(LocalDate.now());
        ordRepo.create(ord);

        if (ord.getIdOrd() == null) {
            throw new ServiceException("Erreur critique : L'ID de l'ordonnance n'a pas été généré.");
        }

        // 3. Création des lignes de prescription liées
        for (Prescription p : prescriptions) {
            // Validation interne de chaque ligne
            Validators.checkNotNull(p.getIdMct(), "Le médicament est manquant dans une ligne.");
            Validators.checkNotEmpty(p.getFrequence(), "La fréquence est obligatoire (ex: Matin/Soir).");

            p.setIdOrd(ord.getIdOrd()); // Liaison FK
            prescRepo.create(p);
        }
    }

    @Override
    public List<Ordonnance> getHistoriquePatient(Long idPatient) {
        return ordRepo.findByPatientId(idPatient);
    }

    @Override
    public List<Prescription> getDetailsOrdonnance(Long idOrdonnance) {
        return prescRepo.findByOrdonnanceId(idOrdonnance);
    }

    @Override
    public void supprimerOrdonnance(Long idOrd) throws ServiceException {
        // On supprime d'abord les lignes (Prescriptions) pour respecter les FK
        List<Prescription> lignes = prescRepo.findByOrdonnanceId(idOrd);
        for (Prescription p : lignes) {
            prescRepo.deleteById(p.getIdPr());
        }
        // Puis l'ordonnance
        ordRepo.deleteById(idOrd);
    }
    @Override
    public ma.dentalTech.entities.ordonnance.Ordonnance getOrdonnanceById(Long id) {
        return ordRepo.findById(id);
    }
}
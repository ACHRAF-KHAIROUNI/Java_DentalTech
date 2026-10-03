package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepo;

    public PatientServiceImpl() {
        this.patientRepo = new PatientRepositoryImpl();
    }

    public PatientServiceImpl(PatientRepository repo) {
        this.patientRepo = repo;
    }

    @Override
    public Patient ajouterPatient(Patient patient) throws ServiceException {
        validerDonneesPatient(patient);

        if (patient.getTelephone() != null && !patient.getTelephone().isBlank()) {
            Optional<Patient> existe = patientRepo.findByTelephone(patient.getTelephone());
            if (existe.isPresent()) {
                throw new ServiceException("Téléphone déjà utilisé par : " + existe.get().getNom());
            }
        }

        try {
            patientRepo.create(patient);
            return patient;
        } catch (Exception e) {
            throw new ServiceException("Erreur création patient : " + e.getMessage());
        }
    }

    @Override
    public Patient modifierPatient(Patient patient) throws ServiceException {
        if (patient.getIdPatient() == null) throw new ServiceException("ID manquant.");
        validerDonneesPatient(patient);

        if (patientRepo.findById(patient.getIdPatient()) == null) {
            throw new ServiceException("Patient introuvable.");
        }

        try {
            patientRepo.update(patient);
            return patient;
        } catch (Exception e) {
            throw new ServiceException("Erreur modification patient.");
        }
    }

    @Override
    public void supprimerPatient(Long idPatient) throws ServiceException {
        try {
            patientRepo.deleteById(idPatient);
        } catch (Exception e) {
            throw new ServiceException("Impossible de supprimer (liens existants).");
        }
    }

    @Override
    public Patient getPatientById(Long id) throws ServiceException {
        return patientRepo.findById(id);
    }

    @Override
    public List<Patient> getAllPatients() throws ServiceException {
        return patientRepo.findAll();
    }

    @Override
    public List<Patient> rechercherPatients(String motCle) throws ServiceException {
        if (motCle == null || motCle.isBlank()) return getAllPatients();
        return patientRepo.searchByNomOrPrenom(motCle);
    }

    @Override
    public boolean patientExiste(String telephone) {
        return patientRepo.findByTelephone(telephone).isPresent();
    }

    @Override
    public int calculerAge(Long idPatient) throws ServiceException {
        Patient p = getPatientById(idPatient);
        if (p == null || p.getDateDeNaissance() == null) return 0;
        return Period.between(p.getDateDeNaissance(), LocalDate.now()).getYears();
    }

    @Override
    public String getResumePatient(Long idPatient) throws ServiceException {
        Patient p = getPatientById(idPatient);
        if (p == null) throw new ServiceException("Patient introuvable.");

        int age = (p.getDateDeNaissance() != null) ? Period.between(p.getDateDeNaissance(), LocalDate.now()).getYears() : 0;

        return String.format("%s %s - %d ans - %s",
                p.getNom().toUpperCase(), p.getPrenom(), age, p.getAssurance());
    }

    // --- NOUVELLE MÉTHODE POUR MVC ---
    @Override
    public List<PatientDTO> getTodayPatientsAsDTO() {
        try {
            List<Patient> all = patientRepo.findAll();
            LocalDate today = LocalDate.now();

            return all.stream()
                    // Filtre : Uniquement ceux créés aujourd'hui
                    .filter(p -> p.getDateCreation() != null && p.getDateCreation().equals(today))
                    // Transformation : Patient -> PatientDTO
                    .map(p -> {
                        int age = 0;
                        if (p.getDateDeNaissance() != null) {
                            age = Period.between(p.getDateDeNaissance(), today).getYears();
                        }
                        return PatientDTO.builder()
                                .nomComplet(p.getNom().toUpperCase() + " " + p.getPrenom())
                                .age(age)
                                .dateCreationFormatee(p.getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erreur récupération DTO: " + e.getMessage());
            return List.of();
        }
    }

    private void validerDonneesPatient(Patient p) throws ServiceException {
        if (p == null) throw new ServiceException("Données vides.");
        if (p.getNom() == null || p.getNom().trim().isEmpty()) throw new ServiceException("Nom obligatoire.");
        if (p.getPrenom() == null || p.getPrenom().trim().isEmpty()) throw new ServiceException("Prénom obligatoire.");
        if (p.getDateDeNaissance() != null && p.getDateDeNaissance().isAfter(LocalDate.now())) {
            throw new ServiceException("Date de naissance invalide (futur).");
        }
    }
}
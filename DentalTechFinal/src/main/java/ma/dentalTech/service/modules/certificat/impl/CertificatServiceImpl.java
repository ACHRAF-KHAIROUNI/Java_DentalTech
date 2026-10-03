package ma.dentalTech.service.modules.certificat.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.certificat.api.CertificatRepository;
import ma.dentalTech.repository.modules.certificat.impl.mySQL.CertificatRepositoryImpl;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.service.modules.certificat.api.CertificatService;

import java.time.LocalDate;
import java.util.List;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepository certificatRepo = new CertificatRepositoryImpl();
    // Besoin du PatientRepo pour récupérer le nom du patient lors de l'impression
    private final PatientRepository patientRepo = new PatientRepositoryImpl();

    @Override
    public void genererCertificat(Certificat c) throws ServiceException {
        // Validation
        if (c.getIdPatient() == null) throw new ServiceException("Patient obligatoire.");
        if (c.getDuree() == null || c.getDuree() <= 0) throw new ServiceException("Durée invalide.");

        // Dates
        if (c.getDateDebut() == null) c.setDateDebut(LocalDate.now());
        c.setDateFin(c.getDateDebut().plusDays(c.getDuree()));


        // On vérifie si le patient a déjà un certificat en cours sur cette période
        List<Certificat> existants = certificatRepo.findByPatientId(c.getIdPatient());
        for (Certificat ex : existants) {
            // Si la nouvelle période (StartA, EndA) chevauche une existante (StartB, EndB)
            // (StartA <= EndB) and (EndA >= StartB)
            if (!c.getDateDebut().isAfter(ex.getDateFin()) && !c.getDateFin().isBefore(ex.getDateDebut())) {
                throw new ServiceException("Attention : Ce patient a déjà un certificat valide du "
                        + ex.getDateDebut() + " au " + ex.getDateFin());
            }
        }

        try {
            certificatRepo.create(c);
        } catch (Exception e) {
            throw new ServiceException("Erreur création certificat : " + e.getMessage());
        }
    }

    @Override
    public List<Certificat> getCertificatsPatient(Long idPatient) throws ServiceException {
        return certificatRepo.findByPatientId(idPatient);
    }
    @Override
    public Certificat consulterCertificat(Long idCertificat) throws ServiceException {
        if (idCertificat == null) throw new ServiceException("ID Certificat obligatoire.");

        Certificat c = certificatRepo.findById(idCertificat);
        if (c == null) {
            throw new ServiceException("Certificat introuvable.");
        }
        return c;
    }

    @Override
    public void supprimerCertificat(Long idCertificat) throws ServiceException {
        // Logique de sécurité : On ne devrait peut-être pas supprimer un certificat légal.
        // Mais pour l'exercice, on le garde.
        try {
            certificatRepo.deleteById(idCertificat);
        } catch (Exception e) {
            throw new ServiceException("Erreur suppression.");
        }
    }



    @Override
    public void corrigerCertificat(Long idCertif, Integer nouvelleDuree, String nouvelleNote) throws ServiceException {
        Certificat c = certificatRepo.findById(idCertif);
        if (c == null) throw new ServiceException("Certificat introuvable.");

        if (nouvelleDuree != null && nouvelleDuree > 0) {
            c.setDuree(nouvelleDuree);
            // Recalcul auto de la date de fin
            c.setDateFin(c.getDateDebut().plusDays(nouvelleDuree));
        }

        if (nouvelleNote != null) {
            c.setNoteMedecin(nouvelleNote);
        }

        certificatRepo.update(c);
    }

    @Override
    public String apercuImpression(Long idCertificat) throws ServiceException {
        Certificat c = certificatRepo.findById(idCertificat);
        if (c == null) throw new ServiceException("Certificat introuvable.");

        Patient p = patientRepo.findById(c.getIdPatient());
        String nomPatient = (p != null) ? p.getNom() + " " + p.getPrenom() : "Inconnu";

        // Simulation de mise en page
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("          CERTIFICAT MEDICAL             \n");
        sb.append("=========================================\n\n");
        sb.append("Je soussigné, Docteur Dentiste,\n");
        sb.append("Certifie avoir examiné le patient : ").append(nomPatient).append("\n");
        sb.append("Et prescris un repos de ").append(c.getDuree()).append(" jours.\n");
        sb.append("Du : ").append(c.getDateDebut()).append("\n");
        sb.append("Au : ").append(c.getDateFin()).append("\n\n");
        if (c.getNoteMedecin() != null && !c.getNoteMedecin().isEmpty()) {
            sb.append("Note : ").append(c.getNoteMedecin()).append("\n");
        }
        sb.append("\nFait à Casablanca, le ").append(LocalDate.now()).append("\n");
        sb.append("Signature : __________________\n");

        return sb.toString();
    }

    @Override
    public boolean isPatientEnArretMaladie(Long idPatient) throws ServiceException {
        List<Certificat> certifs = certificatRepo.findByPatientId(idPatient);
        LocalDate aujourdhui = LocalDate.now();

        for (Certificat c : certifs) {
            // Si aujourd'hui est après le début ET avant la fin
            if (!aujourdhui.isBefore(c.getDateDebut()) && !aujourdhui.isAfter(c.getDateFin())) {
                return true;
            }
        }
        return false;
    }
}
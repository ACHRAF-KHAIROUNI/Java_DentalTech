/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.controllers.modules.dossierMedical.swing_implementation;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedical.impl.ConsultationServiceImpl;
import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.api.PatientService;
import ma.dentalTech.service.modules.patient.impl.AntecedentServiceImpl;
import ma.dentalTech.service.modules.patient.impl.PatientServiceImpl;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DossierMedicalControllerImpl implements DossierMedicalController {

    private final PatientService patientService = new PatientServiceImpl();
    private final AntecedentService antecedentService = new AntecedentServiceImpl();
    private final ConsultationService consultationService = new ConsultationServiceImpl();
    private final ma.dentalTech.service.modules.ordonnance.api.OrdonnanceService ordonnanceService = new ma.dentalTech.service.modules.ordonnance.impl.OrdonnanceServiceImpl(
            new ma.dentalTech.repository.modules.ordonnance.impl.mySQL.OrdonnanceRepositoryImpl(),
            new ma.dentalTech.repository.modules.ordonnance.impl.mySQL.PrescriptionRepositoryImpl()
    );
    private final ma.dentalTech.service.modules.certificat.api.CertificatService certificatService = new ma.dentalTech.service.modules.certificat.impl.CertificatServiceImpl();

    // Répertoire de stockage des documents (dans le dossier utilisateur)
    private static final String DOCS_ROOT = System.getProperty("user.home") + File.separator + "DentalTechDocuments";

    @Override
    public DossierMedical getDossierComplet(Long idPatient) {
        DossierMedical dossier = new DossierMedical();
        
        try {
            // 1. Charger Patient (Optimisation: Use getPatientById instead of streaming all)
            Patient p = patientService.getPatientById(idPatient);
            dossier.setPatient(p);

            if (p != null) {
                // 2. Charger Antécédents
                dossier.setAntecedents(antecedentService.getAntecedentsByPatient(idPatient));

                // 3. Charger Consultations (Historique)
                dossier.setConsultations(consultationService.getConsultationsByPatient(idPatient));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du chargement du dossier : " + e.getMessage());
        }
        return dossier;
    }

    @Override
    public List<ma.dentalTech.entities.ordonnance.Ordonnance> getOrdonnances(Long idPatient) {
        try {
            return ordonnanceService.getHistoriquePatient(idPatient);
        } catch (Exception e) { return new ArrayList<>(); }
    }

    @Override
    public List<ma.dentalTech.entities.certificat.Certificat> getCertificats(Long idPatient) {
        try {
             return certificatService.getCertificatsPatient(idPatient);
        } catch (Exception e) { return new ArrayList<>(); }
    }

    @Override
    public void uploadDocument(Long idPatient, File sourceFile) {
        if(idPatient == null || sourceFile == null) return;
        
        try {
            // Créer dossier patient s'il n'existe pas
            File patientDir = new File(DOCS_ROOT + File.separator + idPatient);
            if (!patientDir.exists()) patientDir.mkdirs();

            // Copier le fichier
            File destFile = new File(patientDir, sourceFile.getName());
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            JOptionPane.showMessageDialog(null, "Document ajouté avec succès !");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la copie du fichier : " + e.getMessage(), "Erreur IO", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public List<File> getDocuments(Long idPatient) {
        File patientDir = new File(DOCS_ROOT + File.separator + idPatient);
        if (patientDir.exists() && patientDir.isDirectory()) {
            File[] files = patientDir.listFiles();
            if (files != null) return Arrays.asList(files);
        }
        return new ArrayList<>();
    }

    private final ma.dentalTech.repository.modules.rdvPatient.api.RDVRepository rdvRepo = new ma.dentalTech.repository.modules.rdvPatient.impl.mySQL.RDVRepositoryImpl();

    @Override
    public void addAntecedent(Long idPatient, ma.dentalTech.entities.patient.Antecedent a) {
        try {
            a.setIdPatient(idPatient);
            antecedentService.ajouterAntecedent(a);
            JOptionPane.showMessageDialog(null, "Antécédent ajouté.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur: " + e.getMessage());
        }
    }

    @Override
    public void createConsultation(Long idPatient, String observation) {
        try {
            // 1. Créer un RDV "Technique" pour la consultation immédiate
            ma.dentalTech.entities.rdvPatient.RDV rdv = ma.dentalTech.entities.rdvPatient.RDV.builder()
                    .date(java.time.LocalDate.now())
                    .heure(java.time.LocalTime.now())
                    .motif("Consultation Immédiate")
                    .statut(ma.dentalTech.entities.enums.StatutRDV.TERMINE)
                    .idPatient(idPatient)
                    .build();
            
            rdvRepo.create(rdv); // create doit setter l'ID dans l'objet rdv
            
            // 2. Créer la consultation
            ma.dentalTech.entities.dossierMedical.Consultation c = ma.dentalTech.entities.dossierMedical.Consultation.builder()
                    .date(java.time.LocalDate.now())
                    .statut("TERMINÉE")
                    .observationMedecin(observation)
                    .idRDV(rdv.getIdRDV())
                    .build();
            
            consultationService.createConsultation(c);
            JOptionPane.showMessageDialog(null, "Consultation enregistrée avec succès.");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur création consultation: " + e.getMessage());
        }
    }

    @Override
    public void deleteDocument(File file) {
        if(file != null && file.exists()) {
            if(file.delete()) {
                JOptionPane.showMessageDialog(null, "Document supprimé.");
            } else {
                JOptionPane.showMessageDialog(null, "Impossible de supprimer le fichier.");
            }
        }
    }
}

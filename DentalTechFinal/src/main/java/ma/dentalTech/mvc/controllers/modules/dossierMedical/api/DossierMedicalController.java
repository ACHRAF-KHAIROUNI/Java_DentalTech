/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.controllers.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import java.io.File;
import java.util.List;

public interface DossierMedicalController {
    DossierMedical getDossierComplet(Long idPatient);
    
    // Listes complémentaires
    List<ma.dentalTech.entities.ordonnance.Ordonnance> getOrdonnances(Long idPatient);
    List<ma.dentalTech.entities.certificat.Certificat> getCertificats(Long idPatient);
    
    // Actions
    void addAntecedent(Long idPatient, ma.dentalTech.entities.patient.Antecedent a);
    void createConsultation(Long idPatient, String observation);
    
    // Gestion Documents
    void uploadDocument(Long idPatient, File file);
    List<File> getDocuments(Long idPatient);
    void deleteDocument(File file);
}

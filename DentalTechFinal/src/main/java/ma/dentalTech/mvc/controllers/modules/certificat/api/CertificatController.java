package ma.dentalTech.mvc.controllers.modules.certificat.api;

import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.mvc.dto.CertificatDTO;
import java.util.List;

public interface CertificatController {

    // Ouvrir la fenêtre de gestion pour un patient donné
    void openCertificatManager(Long idPatient);

    // Actions (Service)
    void createCertificat(Certificat certificat);
    void updateCertificat(Long idCertif, Integer duree, String note);
    void deleteCertificat(Long idCertif);

    // Fonctionnalité métier "Aperçu Impression"
    void previewPrint(Long idCertif);

    // Récupération des données pour le tableau
    List<CertificatDTO> getCertificatsByPatient(Long idPatient);

    // Vérification rapide (Service isPatientEnArretMaladie)
    boolean checkArretMaladie(Long idPatient);
}
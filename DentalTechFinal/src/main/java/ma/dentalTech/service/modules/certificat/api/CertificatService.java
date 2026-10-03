package ma.dentalTech.service.modules.certificat.api;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.certificat.Certificat;

import java.util.List;

public interface CertificatService {

    // --- EXISTANT ---
    void genererCertificat(Certificat certificat) throws ServiceException;
    List<Certificat> getCertificatsPatient(Long idPatient) throws ServiceException;
    void supprimerCertificat(Long idCertificat) throws ServiceException;




     //Permet de corriger une durée ou une note (recalcule la date de fin).
    void corrigerCertificat(Long idCertif, Integer nouvelleDuree, String nouvelleNote) throws ServiceException;


    //Génère le texte officiel du certificat médical.
    String apercuImpression(Long idCertificat) throws ServiceException;

    Certificat consulterCertificat(Long idCertificat) throws ServiceException;

     // Vérifie si le patient est actuellement en arrêt maladie (date du jour comprise entre debut et fin d'un certif)
    boolean isPatientEnArretMaladie(Long idPatient) throws ServiceException;
}

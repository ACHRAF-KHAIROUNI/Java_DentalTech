package ma.dentalTech.mvc.controllers.modules.certificat.batch_implentation;

import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.mvc.controllers.modules.certificat.api.CertificatController;
import ma.dentalTech.mvc.dto.CertificatDTO;
import ma.dentalTech.service.modules.certificat.api.CertificatService;
import ma.dentalTech.service.modules.certificat.impl.CertificatServiceImpl;

import java.util.List;

public class CertificatControllerImpl implements CertificatController {

    private CertificatService service = new CertificatServiceImpl();

    @Override
    public void openCertificatManager(Long idPatient) {
        System.out.println(">>> BATCH: Gestion Certificats Patient " + idPatient);
    }

    @Override
    public void createCertificat(Certificat certificat) {
        try {
            service.genererCertificat(certificat);
            System.out.println("Certificat créé.");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @Override
    public void updateCertificat(Long idCertif, Integer duree, String note) {
        try {
            service.corrigerCertificat(idCertif, duree, note);
            System.out.println("Certificat modifié.");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @Override
    public void deleteCertificat(Long idCertif) {
        try {
            service.supprimerCertificat(idCertif);
            System.out.println("Certificat supprimé.");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @Override
    public void previewPrint(Long idCertif) {
        try {
            System.out.println(service.apercuImpression(idCertif));
        } catch (Exception e) {
            System.err.println("Erreur impression: " + e.getMessage());
        }
    }

    @Override
    public List<CertificatDTO> getCertificatsByPatient(Long idPatient) {
        return List.of();
    }

    @Override
    public boolean checkArretMaladie(Long idPatient) {
        return false;
    }
}
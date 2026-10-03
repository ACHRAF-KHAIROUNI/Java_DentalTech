package ma.dentalTech.mvc.controllers.modules.certificat.swing_implementation;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.mvc.controllers.modules.certificat.api.CertificatController;
import ma.dentalTech.mvc.dto.CertificatDTO;
import ma.dentalTech.mvc.ui.modules.certificat.CertificatView;
import ma.dentalTech.service.modules.certificat.api.CertificatService;
import ma.dentalTech.service.modules.certificat.impl.CertificatServiceImpl;

@Data @AllArgsConstructor @NoArgsConstructor
public class CertificatControllerImpl implements CertificatController {

    private CertificatService service = new CertificatServiceImpl();

    @Override
    public void openCertificatManager(Long idPatient) {
        try {
            CertificatView view = new CertificatView(this, idPatient);
            view.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createCertificat(Certificat certificat) {
        try {
            service.genererCertificat(certificat);
            JOptionPane.showMessageDialog(null, "Certificat généré avec succès !");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void updateCertificat(Long idCertif, Integer duree, String note) {
        try {
            service.corrigerCertificat(idCertif, duree, note);
            JOptionPane.showMessageDialog(null, "Certificat corrigé.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public void deleteCertificat(Long idCertif) {
        try {
            service.supprimerCertificat(idCertif);
            JOptionPane.showMessageDialog(null, "Certificat supprimé.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public void previewPrint(Long idCertif) {
        try {
            String texte = service.apercuImpression(idCertif);
            // On affiche le texte dans une belle zone de texte
            javax.swing.JTextArea textArea = new javax.swing.JTextArea(texte);
            textArea.setEditable(false);
            textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

            JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(textArea), "Aperçu Impression", JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public List<CertificatDTO> getCertificatsByPatient(Long idPatient) {
        try {
            List<Certificat> entities = service.getCertificatsPatient(idPatient);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            return entities.stream().map(c -> CertificatDTO.builder()
                    .idCertif(c.getIdCertif())
                    .idPatient(c.getIdPatient())
                    .duree(c.getDuree())
                    .note(c.getNoteMedecin())
                    .periode("Du " + c.getDateDebut().format(fmt) + " au " + c.getDateFin().format(fmt))
                    .build()
            ).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public boolean checkArretMaladie(Long idPatient) {
        try {
            return service.isPatientEnArretMaladie(idPatient);
        } catch (Exception e) {
            return false;
        }
    }
}
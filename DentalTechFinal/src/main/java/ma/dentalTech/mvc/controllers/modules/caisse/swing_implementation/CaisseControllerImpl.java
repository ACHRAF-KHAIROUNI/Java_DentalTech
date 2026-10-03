/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.controllers.modules.caisse.swing_implementation;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.entities.caisse.Facture;
import ma.dentalTech.entities.caisse.Revenue;
import ma.dentalTech.entities.caisse.SituationFinanciere;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController;
import ma.dentalTech.service.modules.caisse.api.CaisseService;
import ma.dentalTech.service.modules.caisse.impl.CaisseServiceImpl;

import javax.swing.JOptionPane;
import java.util.List;

public class CaisseControllerImpl implements CaisseController {

    private final CaisseService service = new CaisseServiceImpl();

    @Override
    public void createFacture(Facture f) {
        try {
            service.createFacture(f);
            JOptionPane.showMessageDialog(null, "Facture créée avec succès.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void updateFacture(Facture f) {
        try {
            service.updateFacture(f);
            JOptionPane.showMessageDialog(null, "Facture mise à jour.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage());
        }
    }

    @Override
    public void deleteFacture(Long id) {
        try {
            if (JOptionPane.showConfirmDialog(null, "Confirmez-vous la suppression ?", "Confirmer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                service.deleteFacture(id);
                JOptionPane.showMessageDialog(null, "Facture supprimée.");
            }
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Impossible de supprimer : " + e.getMessage());
        }
    }

    @Override
    public void appliquerRemise(Long idFacture, Double valeur, boolean isPourcentage) {
        try {
            service.appliquerRemise(idFacture, valeur, isPourcentage);
            JOptionPane.showMessageDialog(null, "Remise appliquée avec succès !");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public List<ma.dentalTech.mvc.dto.FactureDTO> getAllFactures() {
        return service.getAllFactures().stream()
                .map(f -> ma.dentalTech.mvc.dto.FactureDTO.builder()
                        .id(f.getIdFacture())
                        .date(f.getDateFacture().toLocalDate().toString())
                        .montantTotal(String.format("%.2f DH", f.getTotaleFacture()))
                        .montantPaye(String.format("%.2f DH", f.getTotalePaye()))
                        .reste(String.format("%.2f DH", f.getReste()))
                        .statut(f.getStatut())
                        .idConsultation(f.getIdConsultation())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void payerFacture(Long idFacture, Double montant) {
        try {
            service.enregistrerPaiement(idFacture, montant);
            JOptionPane.showMessageDialog(null, "Paiement enregistré.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Erreur paiement : " + e.getMessage());
        }
    }

    @Override
    public SituationFinanciere getSituation(Long idFacture) {
        return service.getSituationByFacture(idFacture);
    }

    @Override
    public void addCharge(Charge c) {
        try {
            service.addCharge(c);
            JOptionPane.showMessageDialog(null, "Charge ajoutée.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public List<Charge> getAllCharges() {
        return service.getAllCharges();
    }

    @Override
    public void addRevenue(Revenue r) {
        try {
            service.addRevenue(r);
            JOptionPane.showMessageDialog(null, "Revenu ajouté.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public List<Revenue> getAllRevenues() {
        return service.getAllRevenues();
    }
}

/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.controllers.modules.caisse.api;

import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.entities.caisse.Facture;
import ma.dentalTech.entities.caisse.Revenue;
import ma.dentalTech.entities.caisse.SituationFinanciere;
import ma.dentalTech.mvc.dto.FactureDTO;

import java.util.List;

public interface CaisseController {
    void createFacture(Facture f);
    void updateFacture(Facture f);
    void deleteFacture(Long id);
    void appliquerRemise(Long idFacture, Double valeur, boolean isPourcentage);
    List<FactureDTO> getAllFactures();
    void payerFacture(Long idFacture, Double montant);

    SituationFinanciere getSituation(Long idFacture);
    
    void addCharge(Charge c);
    List<Charge> getAllCharges();
    
    void addRevenue(Revenue r);
    List<Revenue> getAllRevenues();
}

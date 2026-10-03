/* Author : Rahali Zakaria */
package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.entities.caisse.Facture;
import ma.dentalTech.entities.caisse.Revenue;
import ma.dentalTech.entities.caisse.SituationFinanciere;
import ma.dentalTech.entities.caisse.Statistique;
import ma.dentalTech.mvc.dto.FactureDTO;

import java.util.List;

public interface CaisseService {

    // --- FACTURES ---
    void createFacture(Facture f) throws ServiceException;
    void updateFacture(Facture f) throws ServiceException;
    void deleteFacture(Long id) throws ServiceException;
    List<Facture> getAllFactures();
    Facture getFactureById(Long id);
    void enregistrerPaiement(Long idFacture, Double montant) throws ServiceException;

    // --- SITUATION FINANCIERE ---
    SituationFinanciere getSituationByFacture(Long idFacture);
    void updateSituation(SituationFinanciere sf);

    // --- JOURNAL (CHARGES / REVENUS) ---
    void addCharge(Charge c) throws ServiceException;
    void addRevenue(Revenue r) throws ServiceException;
    List<Charge> getAllCharges();
    List<Revenue> getAllRevenues();

    // --- DASHBOARD / STATS ---
    Double getChiffreAffairesGlobal();
    Double getRecettesDuMois();
    Double getDepensesDuMois();

    void appliquerRemise(Long idFacture, Double valeur, boolean isPourcentage) throws ServiceException;
}

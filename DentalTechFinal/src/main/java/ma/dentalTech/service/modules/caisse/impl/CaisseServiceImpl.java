/* Author : Rahali Zakaria */
package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.*;
import ma.dentalTech.repository.modules.caisse.impl.mySQL.*;
import ma.dentalTech.service.modules.caisse.api.CaisseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CaisseServiceImpl implements CaisseService {

    /* Direct instantiation (No DI container logic for Swing/JDBC project) */
    private final FactureRepository factureRepo = new FactureRepositoryImpl();
    private final SituationFinanciereRepository situationRepo = new SituationFinanciereRepositoryImpl();
    private final ChargeRepository chargeRepo = new ChargeRepositoryImpl();
    private final RevenueRepository revenueRepo = new RevenueRepositoryImpl();
    // private final StatistiqueRepository statsRepo = new StatistiqueRepositoryImpl();

    @Override
    public void createFacture(Facture f) throws ServiceException {
        if(f.getTotaleFacture() == null || f.getTotaleFacture() < 0)
            throw new ServiceException("Montant total invalide");

        /* Initial Calculations */
        if(f.getTotalePaye() == null) f.setTotalePaye(0.0);
        f.setReste(f.getTotaleFacture() - f.getTotalePaye());
        f.setStatut(f.getReste() <= 0 ? "PAYÉE" : "NON PAYÉE");
        if(f.getDateFacture() == null) f.setDateFacture(LocalDateTime.now());

        factureRepo.create(f);

        /* Auto-create associated Financial Situation */
        SituationFinanciere sf = SituationFinanciere.builder()
                .totaleDesActes(f.getTotaleFacture())
                .totalePaye(f.getTotalePaye())
                .credit(f.getReste())
                .statut(f.getStatut())
                .enPromo("NON")
                .idFacture(f.getIdFacture())
                .build();
        situationRepo.create(sf);
    }

    @Override
    public void updateFacture(Facture f) throws ServiceException {
        /* Auto-recalculation */
        f.setReste(f.getTotaleFacture() - f.getTotalePaye());
        f.setStatut(f.getReste() <= 0 ? "PAYÉE" : "NON PAYÉE");
        factureRepo.update(f);

        /* Update Situation */
        Optional<SituationFinanciere> optSf = situationRepo.findByFactureId(f.getIdFacture());
        if(optSf.isPresent()){
            SituationFinanciere sf = optSf.get();
            sf.setTotaleDesActes(f.getTotaleFacture());
            sf.setTotalePaye(f.getTotalePaye());
            sf.setCredit(f.getReste());
            sf.setStatut(f.getStatut());
            situationRepo.update(sf);
        }
    }

    @Override
    public void deleteFacture(Long id) throws ServiceException {
        /* Delete associated Financial Situation via SQL first to avoid mapping errors */
        situationRepo.deleteByFactureId(id);
        
        /* Delete Invoice */
        factureRepo.deleteById(id);
    }

    @Override
    public List<Facture> getAllFactures() {
        return factureRepo.findAll();
    }

    @Override
    public Facture getFactureById(Long id) {
        return factureRepo.findById(id);
    }

    @Override
    public void enregistrerPaiement(Long idFacture, Double montant) throws ServiceException {
        Facture f = factureRepo.findById(idFacture);
        if(f == null) throw new ServiceException("Facture introuvable");

        f.setTotalePaye(f.getTotalePaye() + montant);
        updateFacture(f); /* Handle recalculation and situation update */
    }

    @Override
    public SituationFinanciere getSituationByFacture(Long idFacture) {
        return situationRepo.findByFactureId(idFacture).orElse(null);
    }

    @Override
    public void updateSituation(SituationFinanciere sf) {
        situationRepo.update(sf);
    }

    @Override
    public void addCharge(Charge c) throws ServiceException {
        if(c.getMontant() <= 0) throw new ServiceException("Montant incorrect");
        if(c.getDate() == null) c.setDate(LocalDateTime.now());
        chargeRepo.create(c);
    }

    @Override
    public void addRevenue(Revenue r) throws ServiceException {
        if(r.getMontant() <= 0) throw new ServiceException("Montant incorrect");
        if(r.getDate() == null) r.setDate(LocalDateTime.now());
        revenueRepo.create(r);
    }

    @Override
    public List<Charge> getAllCharges() {
        return chargeRepo.findAll();
    }

    @Override
    public List<Revenue> getAllRevenues() {
        return revenueRepo.findAll();
    }

    @Override
    public Double getChiffreAffairesGlobal() {
        /* Sum total paid invoices */
        return factureRepo.findAll().stream()
                .mapToDouble(Facture::getTotalePaye)
                .sum();
    }

    @Override
    public Double getRecettesDuMois() {
        /* Simplification: Sum all for now, or filter by date if needed */
        return getAllRevenues().stream().mapToDouble(Revenue::getMontant).sum();
    }

    @Override
    public Double getDepensesDuMois() {
        return getAllCharges().stream().mapToDouble(Charge::getMontant).sum();
    }

    @Override
    public void appliquerRemise(Long idFacture, Double valeur, boolean isPourcentage) throws ServiceException {
        if(valeur <= 0) throw new ServiceException("La valeur de la remise doit être positive.");

        Facture f = factureRepo.findById(idFacture);
        if(f == null) throw new ServiceException("Facture introuvable.");

        double montantRemise = 0.0;
        if (isPourcentage) {
            if (valeur > 100) throw new ServiceException("Le pourcentage ne peut dépasser 100%.");
            montantRemise = f.getTotaleFacture() * (valeur / 100.0);
        } else {
            montantRemise = valeur;
        }

        if(f.getTotaleFacture() < montantRemise)
            throw new ServiceException("La remise (" + montantRemise + ") ne peut pas être supérieure au montant total (" + f.getTotaleFacture() + ").");

        /* Apply Discount */
        f.setTotaleFacture(f.getTotaleFacture() - montantRemise);
        
        /* Recalculate remaining and status via updateFacture */
        updateFacture(f);
    }
}

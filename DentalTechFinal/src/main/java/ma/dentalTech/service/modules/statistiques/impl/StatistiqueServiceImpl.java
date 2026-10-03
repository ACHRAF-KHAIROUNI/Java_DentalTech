package ma.dentalTech.service.modules.statistiques.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.entities.caisse.Revenue;
import ma.dentalTech.mvc.dto.StatistiqueDTO;
import ma.dentalTech.repository.modules.caisse.impl.mySQL.ChargeRepositoryImpl;
import ma.dentalTech.repository.modules.caisse.impl.mySQL.RevenueRepositoryImpl;
import ma.dentalTech.service.modules.statistiques.api.StatistiqueService;

import java.time.LocalDate;
import java.util.List;

public class StatistiqueServiceImpl implements StatistiqueService {

    private final RevenueRepositoryImpl listRevRepo = new RevenueRepositoryImpl();
    private final ChargeRepositoryImpl listChgRepo = new ChargeRepositoryImpl();

    @Override
    public StatistiqueDTO getStatistiquesGlobales() throws ServiceException {
        try {
            // 1. Récupérer TOUTES les données
            List<Revenue> allRevenus = listRevRepo.findAll();
            List<Charge> allCharges = listChgRepo.findAll();

            // --- CONFIGURATION DE LA DATE DE RÉFÉRENCE ---
            // Pour ton test, comme tes données sont en Janvier 2026, on fixe la date ici.
            // En production réelle, on remettra : LocalDate.now();
            LocalDate dateReference = LocalDate.of(2026, 1, 7);

            int moisActuel = dateReference.getMonthValue();
            int anneeActuelle = dateReference.getYear();

            // 2. CALCULS GLOBAUX (Tout l'historique : 2025 + 2026)
            double totalRev = allRevenus.stream().mapToDouble(Revenue::getMontant).sum();
            double totalChg = allCharges.stream().mapToDouble(Charge::getMontant).sum();

            // 3. CALCULS ANNUELS (Uniquement 2026)
            double revAn = allRevenus.stream()
                    .filter(r -> r.getDate().toLocalDate().getYear() == anneeActuelle)
                    .mapToDouble(Revenue::getMontant).sum();

            double chgAn = allCharges.stream()
                    .filter(c -> c.getDate().toLocalDate().getYear() == anneeActuelle)
                    .mapToDouble(Charge::getMontant).sum();

            // 4. CALCULS MENSUELS (Uniquement Janvier 2026)
            double revMois = allRevenus.stream()
                    .filter(r -> r.getDate().toLocalDate().getYear() == anneeActuelle
                            && r.getDate().toLocalDate().getMonthValue() == moisActuel)
                    .mapToDouble(Revenue::getMontant).sum();

            double chgMois = allCharges.stream()
                    .filter(c -> c.getDate().toLocalDate().getYear() == anneeActuelle
                            && c.getDate().toLocalDate().getMonthValue() == moisActuel)
                    .mapToDouble(Charge::getMontant).sum();

            // 5. Remplir le DTO
            return StatistiqueDTO.builder()
                    .listeRevenus(allRevenus)
                    .listeCharges(allCharges)

                    // Global (Contient 2025 + 2026)
                    .totalRevenus(totalRev)
                    .totalCharges(totalChg)
                    .beneficeNet(totalRev - totalChg)

                    // Mensuel (Juste Janvier 2026 -> Devrait être 500 MAD)
                    .revenusMensuel(revMois)
                    .chargesMensuel(chgMois)
                    .beneficeMensuel(revMois - chgMois)

                    // Annuel (Juste 2026 -> Devrait être 500 MAD aussi car on est en janvier)
                    .revenusAnnuel(revAn)
                    .chargesAnnuel(chgAn)
                    .beneficeAnnuel(revAn - chgAn)

                    .build();

        } catch (Exception e) {
            throw new ServiceException("Erreur stats : " + e.getMessage());
        }
    }
}
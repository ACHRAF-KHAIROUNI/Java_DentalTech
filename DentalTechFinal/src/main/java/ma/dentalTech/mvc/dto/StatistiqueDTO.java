package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.entities.caisse.Revenue;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class StatistiqueDTO {
    // Listes complètes (Historique)
    private List<Revenue> listeRevenus;
    private List<Charge> listeCharges;

    // Global (Depuis toujours)
    private double totalRevenus;
    private double totalCharges;
    private double beneficeNet;

    // --- NOUVEAU : Mensuel (Mois courant) ---
    private double beneficeMensuel;
    private double revenusMensuel;
    private double chargesMensuel;

    // --- NOUVEAU : Annuel (Année courante) ---
    private double beneficeAnnuel;
    private double revenusAnnuel;
    private double chargesAnnuel;
}
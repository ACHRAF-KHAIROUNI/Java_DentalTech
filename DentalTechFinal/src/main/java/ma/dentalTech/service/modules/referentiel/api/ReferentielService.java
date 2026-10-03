package ma.dentalTech.service.modules.referentiel.api;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;

import java.util.List;

public interface ReferentielService {

    // Liste des Assurances disponibles (CNOPS, CNSS...)
    List<Assurance> getListeAssurances();

    // Liste des Catégories d'antécédents (ALLERGIE, MEDICAL...)
    List<CategorieAntecedent> getListeCategories();

    // Liste des Niveaux de risque (FAIBLE, ELEVE...)
    List<NiveauRisque> getListeNiveauxRisque();

    // --- "CATALOGUE VIRTUEL" (Option A Améliorée) ---
    // Renvoie une liste de suggestions de maladies fréquentes pour aider à la saisie
    // Cela évite d'avoir à créer une table SQL pour l'instant.
    List<String> getSuggestionsMaladies();
}
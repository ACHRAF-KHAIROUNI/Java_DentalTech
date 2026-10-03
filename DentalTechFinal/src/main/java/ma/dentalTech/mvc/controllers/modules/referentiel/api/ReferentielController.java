package ma.dentalTech.mvc.controllers.modules.referentiel.api;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import java.util.List;

//ROBAII IKRAM
public interface ReferentielController {

    // Méthodes pour alimenter les listes déroulantes (Combo Box)
    Assurance[] getAssurancesForCombo();
    CategorieAntecedent[] getCategoriesForCombo();
    NiveauRisque[] getRisquesForCombo();

    // Méthode pour l'autocomplétion des maladies
    List<String> getMaladiesSuggestions();
}
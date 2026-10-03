package ma.dentalTech.service.modules.referentiel.impl;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.service.modules.referentiel.api.ReferentielService;

import java.util.Arrays;
import java.util.List;

public class ReferentielServiceImpl implements ReferentielService {

    @Override
    public List<Assurance> getListeAssurances() {
        return Arrays.asList(Assurance.values());
    }

    @Override
    public List<CategorieAntecedent> getListeCategories() {
        return Arrays.asList(CategorieAntecedent.values());
    }

    @Override
    public List<NiveauRisque> getListeNiveauxRisque() {
        return Arrays.asList(NiveauRisque.values());
    }

    @Override
    public List<String> getSuggestionsMaladies() {
        // Catalogue statique "Intelligent" pour l'autocomplétion
        return List.of(
                "Diabète Type 1",
                "Diabète Type 2",
                "Hypertension Artérielle",
                "Allergie Pénicilline",
                "Allergie Latex",
                "Asthme",
                "Troubles cardiaques",
                "Hépatite B",
                "Fumeur",
                "Grossesse en cours"
        );
    }
}
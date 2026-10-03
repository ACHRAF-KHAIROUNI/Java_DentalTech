package ma.dentalTech.mvc.controllers.modules.referentiel.swing_implementation;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.mvc.controllers.modules.referentiel.api.ReferentielController;
import ma.dentalTech.service.modules.referentiel.api.ReferentielService;
import ma.dentalTech.service.modules.referentiel.impl.ReferentielServiceImpl;

import java.util.List;

//ROBAII IKRAM

public class ReferentielControllerImpl implements ReferentielController {

    private final ReferentielService service = new ReferentielServiceImpl();

    @Override
    public Assurance[] getAssurancesForCombo() {
        List<Assurance> list = service.getListeAssurances();
        // Convertit la liste en tableau pour JComboBox
        return list.toArray(new Assurance[0]);
    }

    @Override
    public CategorieAntecedent[] getCategoriesForCombo() {
        List<CategorieAntecedent> list = service.getListeCategories();
        return list.toArray(new CategorieAntecedent[0]);
    }

    @Override
    public NiveauRisque[] getRisquesForCombo() {
        List<NiveauRisque> list = service.getListeNiveauxRisque();
        return list.toArray(new NiveauRisque[0]);
    }

    @Override
    public List<String> getMaladiesSuggestions() {
        return service.getSuggestionsMaladies();
    }
}
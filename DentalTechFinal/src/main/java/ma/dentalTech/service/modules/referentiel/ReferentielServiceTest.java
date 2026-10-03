package ma.dentalTech.service.modules.referentiel;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.service.modules.referentiel.api.ReferentielService;
import ma.dentalTech.service.modules.referentiel.impl.ReferentielServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//ROBAII IKRAM
public class ReferentielServiceTest {

    private final ReferentielService service = new ReferentielServiceImpl();

    @Test
    void testListesEnums() {
        System.out.println("--- Test Référentiel Enums ---");

        List<Assurance> assurances = service.getListeAssurances();
        assertFalse(assurances.isEmpty(), "La liste assurances ne doit pas être vide");
        assertTrue(assurances.contains(Assurance.CNSS), "Doit contenir CNSS");

        List<CategorieAntecedent> categories = service.getListeCategories();
        assertFalse(categories.isEmpty());
        System.out.println("[OK] Enums chargés correctement.");
    }

    @Test
    void testCatalogueVirtuel() {
        System.out.println("--- Test Catalogue Maladies ---");

        List<String> maladies = service.getSuggestionsMaladies();
        assertFalse(maladies.isEmpty());

        // Vérifions quelques classiques
        assertTrue(maladies.contains("Diabète Type 2"));
        assertTrue(maladies.contains("Allergie Pénicilline"));

        System.out.println("Catalogue chargé : " + maladies.size() + " suggestions.");
        System.out.println("[OK] Catalogue opérationnel.");
    }
}
package ma.dentalTech.service.modules.medicament;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.service.modules.medicament.api.MedicamentService;
import ma.dentalTech.service.modules.medicament.impl.MedicamentServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//ROBAII IKRAM
public class MedicamentServiceTest {

    private final MedicamentService service = new MedicamentServiceImpl();

    @Test
    void testWorkflowMedicament() {
        System.out.println("=============================================");
        System.out.println("   DÉBUT DU TEST : SERVICE MÉDICAMENT");
        System.out.println("=============================================");

        // Nom unique pour le test
        String nomTest = "TEST_MEDOC_" + System.currentTimeMillis();

        Medicament m = Medicament.builder()
                .nom(nomTest)
                .laboratoire("Labo Test")
                .type("Antibiotique")
                .prixUnitaire(100.0)
                .remboursable(true)
                .build();

        Long id = null;

        try {
            // 1. Ajout
            System.out.print("1. Tentative d'ajout du médicament... ");
            service.ajouterMedicament(m);
            id = m.getIdMct();
            assertNotNull(id, "ID doit être généré");
            System.out.println("[OK] Succès. ID généré : " + id);

            // 2. Test Doublon
            System.out.print("2. Test de la règle 'Doublon interdit'... ");
            Medicament doublon = Medicament.builder().nom(nomTest).prixUnitaire(10.0).build();
            assertThrows(ServiceException.class, () -> service.ajouterMedicament(doublon));
            System.out.println("[OK] Doublon correctement rejeté.");

            // 3. Modification
            System.out.print("3. Modification du prix (100 -> 150)... ");
            m.setPrixUnitaire(150.0);
            service.modifierMedicament(m);
            System.out.println("[OK] Modification effectuée.");

            // 4. Vérification DTO
            System.out.print("4. Vérification de la liste DTO... ");
            var dtos = service.getAllAsDTO();
            boolean found = dtos.stream().anyMatch(d -> d.getNom().equals(nomTest) && d.getPrixAffiche().contains("150"));
            assertTrue(found, "Le DTO doit contenir le médicament mis à jour");
            System.out.println("[OK] Données trouvées dans le DTO.");

        } catch (ServiceException e) {
            System.err.println("\n[ERREUR] " + e.getMessage());
            fail("Le test a échoué.");
        } finally {
            // 5. Nettoyage
            if (id != null) {
                System.out.print("5. Nettoyage (Suppression)... ");
                try {
                    service.supprimerMedicament(id);
                    System.out.println("[OK] Nettoyé.");
                } catch (Exception e) {
                    System.out.println("[INFO] " + e.getMessage());
                }
            }
            System.out.println("=============================================");
            System.out.println("   FIN DU TEST : SUCCÈS");
            System.out.println("=============================================");
        }
    }
}
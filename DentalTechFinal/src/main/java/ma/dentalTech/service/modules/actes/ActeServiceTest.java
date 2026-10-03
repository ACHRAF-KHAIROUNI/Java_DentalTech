package ma.dentalTech.service.modules.actes;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.service.modules.actes.api.ActeService;
import ma.dentalTech.service.modules.actes.impl.ActeServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActeServiceTest {

    private final ActeService service = new ActeServiceImpl();

    @Test
    void testWorkflowActe() {
        System.out.println("=== TEST SERVICE ACTES ===");

        // Code unique (timestamp) pour éviter doublon base
        int codeUnique = (int) (System.currentTimeMillis() % 10000);

        Acte a = Acte.builder()
                .libelle("TEST_ACTE")
                .categorie("Chirurgie")
                .code(codeUnique)
                .prixDeBase(500.0)
                .build();

        Long id = null;

        try {
            System.out.print("1. Ajout Acte... ");
            service.ajouterActe(a);
            id = a.getIdActe();
            assertNotNull(id);
            System.out.println("[OK] ID: " + id);

            System.out.print("2. Test Doublon Code... ");
            Acte doublon = Acte.builder().libelle("Autre").code(codeUnique).prixDeBase(10.0).build();
            assertThrows(ServiceException.class, () -> service.ajouterActe(doublon));
            System.out.println("[OK] Rejeté.");

            System.out.print("3. Modification... ");
            a.setPrixDeBase(600.0);
            service.modifierActe(a);
            System.out.println("[OK]");

        } catch (ServiceException e) {
            fail("Erreur : " + e.getMessage());
        } finally {
            if (id != null) {
                try {
                    service.supprimerActe(id);
                    System.out.println("4. Nettoyage [OK]");
                } catch (Exception e) {}
            }
        }
    }
}
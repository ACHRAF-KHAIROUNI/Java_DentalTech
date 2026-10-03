package ma.dentalTech.service.modules.agenda;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.service.modules.agenda.api.AgendaService;
import ma.dentalTech.service.modules.agenda.impl.AgendaServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgendaServiceTest {

    private final AgendaService agendaService = new AgendaServiceImpl();

    // On utilise l'ID 1 qui existe déjà dans ta base (vu sur ton screenshot)
    private final Long ID_MEDECIN_EXISTANT = 1L;

    @Test
    void testCreerEtConsulterAgenda() throws ServiceException {
        // 1. Préparer une donnée de test unique pour éviter les conflits
        String moisTest = "TEST_NOVEMBRE_2024";

        AgendaMensuel agenda = AgendaMensuel.builder()
                .mois(moisTest)
                .idMedecin(ID_MEDECIN_EXISTANT)
                .etat(true)
                .build();

        Long createdId = null;

        try {
            // 2. Exécuter la création
            agendaService.creerAgendaMensuel(agenda);
            createdId = agenda.getIdAgenda();

            // 3. Vérifications
            assertNotNull(createdId, "L'agenda doit avoir un ID");

            List<AgendaMensuel> liste = agendaService.consulterAgendasMedecin(ID_MEDECIN_EXISTANT);

            // On cherche notre agenda spécifique dans la liste
            boolean found = liste.stream()
                    .anyMatch(a -> a.getMois().equals(moisTest));

            assertTrue(found, "L'agenda créé doit apparaître dans la liste du médecin");

        } finally {
            // 4. NETTOYAGE CIBLÉ (Important !)
            // Quoi qu'il arrive (succès ou échec), on supprime cet agenda pour ne pas polluer la base
            if (createdId != null) {
                agendaService.supprimerAgenda(createdId);
            }
        }
    }

    @Test
    void testModifierEtatAgenda() throws ServiceException {
        // 1. Préparation
        String moisTest = "TEST_JANVIER_2025";
        AgendaMensuel agenda = AgendaMensuel.builder()
                .mois(moisTest)
                .idMedecin(ID_MEDECIN_EXISTANT)
                .etat(true) // Ouvert au début
                .build();

        agendaService.creerAgendaMensuel(agenda);
        Long id = agenda.getIdAgenda();

        try {
            // 2. Action : Fermer l'agenda
            agendaService.modifierEtatAgenda(id, false);

            // 3. Vérification en relisant la base
            List<AgendaMensuel> liste = agendaService.consulterAgendasMedecin(ID_MEDECIN_EXISTANT);
            AgendaMensuel updated = liste.stream()
                    .filter(a -> a.getIdAgenda().equals(id))
                    .findFirst()
                    .orElse(null);

            assertNotNull(updated);
            assertFalse(updated.getEtat(), "L'agenda devrait être fermé (false)");

        } finally {
            // 4. Nettoyage
            if (id != null) {
                agendaService.supprimerAgenda(id);
            }
        }
    }

    @Test
    void testMarquerJourIndisponible() throws ServiceException {
        // 1. Préparation
        AgendaMensuel agenda = AgendaMensuel.builder()
                .mois("TEST_FEVRIER_2025")
                .idMedecin(ID_MEDECIN_EXISTANT)
                .build();

        agendaService.creerAgendaMensuel(agenda);
        Long id = agenda.getIdAgenda();

        try {
            // 2. Action : Marquer un jour futur indisponible
            LocalDate jourFutur = LocalDate.now().plusDays(5);

            // Si la méthode n'est pas encore implémentée dans le repo, cela ne fera rien ou lancera une erreur
            // Si tu as implémenté la simulation (System.out.println), cela passera
            assertDoesNotThrow(() ->
                    agendaService.marquerJourIndisponible(id, jourFutur)
            );

            // Test d'erreur : Marquer un jour passé
            LocalDate jourPasse = LocalDate.now().minusDays(1);
            assertThrows(ServiceException.class, () ->
                            agendaService.marquerJourIndisponible(id, jourPasse),
                    "On ne doit pas pouvoir bloquer une date passée"
            );

        } finally {
            // 3. Nettoyage
            if (id != null) {
                agendaService.supprimerAgenda(id);
            }
        }
    }

    @Test
    void testDoublonMois() throws ServiceException {
        // 1. Créer un premier agenda
        String moisTest = "TEST_DOUBLON";
        AgendaMensuel a1 = AgendaMensuel.builder().mois(moisTest).idMedecin(ID_MEDECIN_EXISTANT).build();
        agendaService.creerAgendaMensuel(a1);

        try {
            // 2. Essayer d'en créer un identique pour le même médecin
            AgendaMensuel a2 = AgendaMensuel.builder().mois(moisTest).idMedecin(ID_MEDECIN_EXISTANT).build();

            ServiceException ex = assertThrows(ServiceException.class, () -> {
                agendaService.creerAgendaMensuel(a2);
            });

            assertTrue(ex.getMessage().contains("existe déjà"), "Devrait détecter le doublon");

        } finally {
            // Nettoyage du premier agenda créé
            if (a1.getIdAgenda() != null) {
                agendaService.supprimerAgenda(a1.getIdAgenda());
            }
        }
    }
}